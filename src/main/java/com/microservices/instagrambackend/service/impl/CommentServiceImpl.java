package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Comment;
import com.microservices.instagrambackend.domain.Like;
import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.CommentPostRequest;
import com.microservices.instagrambackend.dto.LikePostRequest;
import com.microservices.instagrambackend.dto.ListCommentResponse;
import com.microservices.instagrambackend.repository.CommentRepository;
import com.microservices.instagrambackend.repository.PostRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public int commentPost(CommentPostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Post post = postRepository.findById(request.postId()).orElseThrow(
                () -> new RuntimeException("Post not found")
        );
        try {
            commentRepository.save(Comment.builder()
                    .post(post)
                    .user(user)
                    .createdAt(new Date())
                    .content(request.content())
                    .build());
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ListCommentResponse> getListsComment(LikePostRequest request) {
        Post post = postRepository.findById(request.postId()).orElseThrow(
                () -> new RuntimeException("Post not found")
        );
        List<Comment> comments = commentRepository.findAllByPost(post);
        return comments.stream().map(
                comment -> ListCommentResponse.builder()
                        .createdDate(comment.getCreatedAt())
                        .email(comment.getUser().getEmail())
                        .username(comment.getUser().getFullname())
                        .content(comment.getContent())
                        .build()
        ).collect(Collectors.toList());
    }
}
