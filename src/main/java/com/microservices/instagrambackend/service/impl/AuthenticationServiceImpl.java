package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.RefreshToken;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.JwtAuthenticationResponse;
import com.microservices.instagrambackend.dto.RefreshTokenRequest;
import com.microservices.instagrambackend.dto.SignInRequest;
import com.microservices.instagrambackend.dto.SignUpRequest;
import com.microservices.instagrambackend.enums.Role;
import com.microservices.instagrambackend.repository.RefreshTokenRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.AuthenticationService;
import com.microservices.instagrambackend.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JWTService jwtService;
    RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void signup(SignUpRequest request) {
        User user = User.builder()
                .fullname(request.fullname())
                .email(request.email())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.password()))
                .createdAt(new Date())
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse signin(SignInRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password())
            );

            User user = userRepository.findByEmail(request.email()).orElseThrow(
                    () -> new IllegalArgumentException("Invalid email or password")
            );

            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
            refreshTokenRepository.save(RefreshToken.builder()
                    .token(refreshToken)
                    .expiryDate(Instant.ofEpochSecond(60 * 60 * 24 * 365))
                    .user(user)
                    .build());

            return JwtAuthenticationResponse.builder()
                    .token(jwt)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error in signin function: {}",e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = jwtService.extractUsername(request.token());
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if(jwtService.isTokenValid(request.token(), user) &&
                refreshTokenRepository.findByToken(request.token()).isPresent()) {

            String jwt = jwtService.generateToken(user);

            return JwtAuthenticationResponse.builder()
                    .token(jwt)
                    .refreshToken(request.token())
                    .build();
        }
        return null;
    }

    @Transactional
    @Override
    public void logout() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        try {
            refreshTokenRepository.deleteByUser(user);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
