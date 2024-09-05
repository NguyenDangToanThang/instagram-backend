package com.microservices.instagrambackend.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PostResponse {
    String id;
    String contentUrl;
    String caption;
    Date createdAt;
    String username;
    String email;
    String avatar;
    Integer quantityLike;
    Integer quantityComment;
    boolean like;
}
