package com.microservices.instagrambackend.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ListCommentResponse {
    String username;
    String email;
    Date createdDate;
    String content;
}
