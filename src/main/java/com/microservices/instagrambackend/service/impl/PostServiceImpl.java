package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.PostResponse;
import com.microservices.instagrambackend.dto.PostUpdateCaptionRequest;
import com.microservices.instagrambackend.repository.FollowRepository;
import com.microservices.instagrambackend.repository.LikeRepository;
import com.microservices.instagrambackend.repository.PostRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.ImageService;
import com.microservices.instagrambackend.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final ImageService imageService;

    @Transactional
    @Override
    public void createPost(MultipartFile image, String caption) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        String contentUrl = null;
        if(image != null) {
            try {
                String fileName = imageService.save(image);
                contentUrl = imageService.getImageUrl(fileName);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        postRepository.save(Post.builder()
                .user(user)
                .contentUrl(contentUrl)
                .createdAt(new Date())
                .caption(caption)
                .build()
        );
    }

    @Transactional
    @Override
    public void deletePost(String postId) {
        try {
            postRepository.deleteById(postId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void updateCaptionPost(PostUpdateCaptionRequest request) {
        Post oldPost = postRepository.findById(request.id()).orElseThrow(
                () -> new RuntimeException("Post not found")
        );

        oldPost.setCaption(request.caption());
        try {
            postRepository.save(oldPost);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<PostResponse> getAllPost(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        final Page<Post> page = postRepository.findAll(pageable);
        List<PostResponse> list = page.getContent()
                .stream()
                .map(post -> {
                    assert user != null;
                    return PostResponse.builder()
                            .caption(post.getCaption())
                            .id(post.getId())
                            .contentUrl(post.getContentUrl())
                            .createdAt(post.getCreatedAt())
                            .username(post.getUser().getFullname())
                            .email(post.getUser().getEmail())
                            .quantityLike(post.getLikes().size())
                            .avatar(post.getUser().getAvatar())
                            .quantityComment(post.getComments().size())
                            .like(likeRepository.existsByUserAndPost(user, post))
                            .follow(Objects.equals(user.getId(), post.getUser().getId()) || followRepository.existsByFolloweeIdAndFollowerId(
                                    user.getId(),
                                    post.getUser().getId()
                            ))
                            .build();
                })
                .collect(Collectors.toList());
        return new PageImpl<>(
                list,
                pageable,
                page.getTotalElements());
    }
}
