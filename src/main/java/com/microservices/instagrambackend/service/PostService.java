package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.dto.PostRequest;
import com.microservices.instagrambackend.dto.PostResponse;
import com.microservices.instagrambackend.dto.PostUpdateCaptionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostService {
    void createPost(PostRequest request);
    void deletePost(String postId);
    void updateCaptionPost(PostUpdateCaptionRequest request);
    Page<PostResponse> getAllPost(Pageable pageable);
}
