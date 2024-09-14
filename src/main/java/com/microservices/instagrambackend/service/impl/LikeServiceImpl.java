package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Like;
import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.LikePostRequest;
import com.microservices.instagrambackend.dto.ListLikeResponse;
import com.microservices.instagrambackend.repository.FollowRepository;
import com.microservices.instagrambackend.repository.LikeRepository;
import com.microservices.instagrambackend.repository.PostRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.LikeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;

    @Transactional
    @Override
    public int likePost(String postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RuntimeException("Post not found")
        );
        Like like = likeRepository.findByUserAndPost(user, post).orElse(null);
        try {
            if(like == null) {
                likeRepository.save(Like.builder()
                        .post(post)
                        .user(user)
                        .createdDate(new Date())
                        .build());
                return 1;
            } else {
                likeRepository.delete(like);
                return -1;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ListLikeResponse> getListsLike(LikePostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        List<Like> list = likeRepository.findAllByPost(
                postRepository.findById(request.postId()).orElse(null)
        );

        return list.stream()
                .filter(like -> !like.getUser().getId().equals(user.getId()))
                .map((like) -> {
                    boolean follow =
                            followRepository.existsByFolloweeIdAndFollowerId(
                                    user.getId(),
                                    like.getUser().getId()
                            );
                    return ListLikeResponse.builder()
                            .id(like.getId())
                            .avatar(like.getUser().getAvatar())
                            .username(like.getUser().getFullname())
                            .email(like.getUser().getEmail())
                            .follow(follow)
                            .build();
        }
        ).collect(Collectors.toList());
    }
}
