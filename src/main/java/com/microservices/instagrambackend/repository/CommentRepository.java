package com.microservices.instagrambackend.repository;

import com.microservices.instagrambackend.domain.Comment;
import com.microservices.instagrambackend.domain.Like;
import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findAllByPost(Post post);
}
