package com.microservices.instagrambackend.repository;

import com.microservices.instagrambackend.domain.Like;
import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, String> {
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    List<Like> findAllByPost(Post post);
}
