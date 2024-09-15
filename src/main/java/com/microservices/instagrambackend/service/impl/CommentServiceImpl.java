package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Comment;
import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.CommentPostRequest;
import com.microservices.instagrambackend.dto.LikePostRequest;
import com.microservices.instagrambackend.dto.ListCommentResponse;
import com.microservices.instagrambackend.dto.ReplyCommentResponse;
import com.microservices.instagrambackend.repository.CommentRepository;
import com.microservices.instagrambackend.repository.PostRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(request.postId());
        return comments.stream().map(
                comment -> ListCommentResponse.builder()
                        .createdDate(comment.getCreatedAt())
                        .email(comment.getUser().getEmail())
                        .username(comment.getUser().getFullname())
                        .avatar(comment.getUser().getAvatar())
                        .content(comment.getContent())
                        .countReply(commentRepository.findByIdWithReplies(
                                comment.getId()
                            ).getReplies().size()
                        )
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public ListCommentResponse getCommentWithReplies(String commentId) {
        Comment rootComment = commentRepository.findByIdWithReplies(commentId);
        if (rootComment == null) {
            throw new RuntimeException("Comment not found");
        }

        ListCommentResponse response = ListCommentResponse.fromEntity(rootComment);

        response.setReplies(getAllRepliesRecursively(rootComment));

        return response;
    }

    @Override
    public ReplyCommentResponse createReply(String parentCommentId, CommentPostRequest reply) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Post post = postRepository.findById(reply.postId()).orElseThrow(
                () -> new RuntimeException("Post not found")
        );
        return ReplyCommentResponse.fromEntity(
                commentRepository.save(
                        Comment.builder()
                                .post(post)
                                .user(user)
                                .createdAt(new Date())
                                .content(reply.content())
                                .parentComment(parentComment)
                .build())
        );
    }

    private List<ReplyCommentResponse> getAllRepliesRecursively(Comment comment) {
        List<ReplyCommentResponse> allReplies = new ArrayList<>();

        for (Comment reply : comment.getReplies()) {
            ReplyCommentResponse replyResponse = ReplyCommentResponse.fromEntity(reply);
            allReplies.add(replyResponse);

            allReplies.addAll(getAllRepliesRecursively(reply));
        }

        return allReplies;
    }
}
