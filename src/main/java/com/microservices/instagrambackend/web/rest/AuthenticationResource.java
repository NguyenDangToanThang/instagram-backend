package com.microservices.instagrambackend.web.rest;

import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.*;
import com.microservices.instagrambackend.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationResource {
    AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseObject<?>> signup (@RequestBody SignUpRequest request) {
        authenticationService.signup(request);
        return ResponseEntity.ok(ResponseObject.success());
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseObject<JwtAuthenticationResponse>> signin (@RequestBody SignInRequest request) {
        return ResponseEntity.ok(ResponseObject.success(authenticationService.signin(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject<JwtAuthenticationResponse>> refresh (@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ResponseObject.success(authenticationService.refreshToken(request)));
    }
}
