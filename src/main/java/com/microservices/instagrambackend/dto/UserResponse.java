package com.microservices.instagrambackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String fullname;
    private String avatar;
    private String bio;
    private Date createdAt;
    private Integer follower;
}
