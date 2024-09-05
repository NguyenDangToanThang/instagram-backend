package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.dto.JwtAuthenticationResponse;
import com.microservices.instagrambackend.dto.RefreshTokenRequest;
import com.microservices.instagrambackend.dto.SignInRequest;
import com.microservices.instagrambackend.dto.SignUpRequest;

public interface AuthenticationService {
    void signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SignInRequest request);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest request);
}
