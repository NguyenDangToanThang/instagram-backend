package com.microservices.instagrambackend.repository;

import com.microservices.instagrambackend.domain.RefreshToken;
import com.microservices.instagrambackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
}
