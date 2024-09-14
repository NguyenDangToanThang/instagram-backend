package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.dto.FollowerResponse;

import java.util.List;

public interface FollowService {
    void followUser(String email);
    List<FollowerResponse> followUsers();
}
