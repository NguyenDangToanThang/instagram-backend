package com.microservices.instagrambackend.service.impl;

import com.microservices.instagrambackend.domain.Follow;
import com.microservices.instagrambackend.domain.User;
import com.microservices.instagrambackend.repository.FollowRepository;
import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.FollowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void followUser(String email) {
        String emailCurrentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailCurrentUser).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        User userFollow = userRepository.findByEmail(email).orElseThrow(
                () ->new RuntimeException("User not found")
        );
        try {
            followRepository.save(Follow.builder()
                .followerId(userFollow.getId())
                .followeeId(user.getId())
                .createdDate(new Date())
                .build());}
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
