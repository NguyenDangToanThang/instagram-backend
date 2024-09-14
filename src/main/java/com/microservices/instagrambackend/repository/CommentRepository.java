package com.microservices.instagrambackend.repository;

import com.microservices.instagrambackend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostIdAndParentCommentIsNull(String postId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.id = :commentId")
    Comment findByIdWithReplies(String commentId);
}
