package com.microservices.instagrambackend.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class JwtAuthenticationResponse {
    String token;
    String refreshToken;
}