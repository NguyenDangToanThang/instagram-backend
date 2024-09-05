package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.dto.LikePostRequest;
import com.microservices.instagrambackend.dto.ListLikeResponse;

import java.util.List;

public interface LikeService {
    int likePost(String postId);
    List<ListLikeResponse> getListsLike(LikePostRequest request);
}
