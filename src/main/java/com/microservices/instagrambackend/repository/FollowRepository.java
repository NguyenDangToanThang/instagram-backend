package com.microservices.instagrambackend.repository;

import com.microservices.instagrambackend.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, String> {
    boolean existsByFolloweeIdAndFollowerId(String followeeId, String followerId);
}
