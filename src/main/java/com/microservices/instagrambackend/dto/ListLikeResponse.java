package com.microservices.instagrambackend.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ListLikeResponse {
    String id;
    String username;
    String email;
    String avatar;
}
