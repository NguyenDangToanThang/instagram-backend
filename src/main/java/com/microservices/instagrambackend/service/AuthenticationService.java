package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.dto.JwtAuthenticationResponse;
import com.microservices.instagrambackend.dto.RefreshTokenRequest;
import com.microservices.instagrambackend.dto.SignInRequest;
import com.microservices.instagrambackend.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    void signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SignInRequest request);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout();
}
