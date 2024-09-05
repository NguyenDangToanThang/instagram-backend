package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Post;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.dto.PostRequest;
import com.microservices.instagrambackend.dto.PostResponse;
import com.microservices.instagrambackend.dto.PostUpdateCaptionRequest;
import com.microservices.instagrambackend.repository.LikeRepository;
import com.microservices.instagrambackend.repository.PostRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public void createPost(PostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);

        String contentUrl = null;
        if(request.image() != null) {
            //handle saving image on cloud in there (some options: firebase storage, cloudinary)
        }
        postRepository.save(Post.builder()
                .user(user)
                .contentUrl(contentUrl)
                .createdAt(new Date())
                .caption(request.caption())
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
                .map(post -> PostResponse.builder()
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
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(
                list,
                pageable,
                page.getTotalElements());
    }
}
