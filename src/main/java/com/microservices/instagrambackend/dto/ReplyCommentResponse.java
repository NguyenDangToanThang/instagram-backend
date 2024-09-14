package com.microservices.instagrambackend.dto;

import com.microservices.instagrambackend.domain.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReplyCommentResponse {
    private String id;
    private String content;
    private Date createdAt;
    private String email;
    private String userName;
    private String avatar;

    public static ReplyCommentResponse fromEntity(Comment reply) {
        return ReplyCommentResponse.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .email(reply.getUser().getEmail())
                .userName(reply.getUser().getFullname())
                .avatar(reply.getUser().getAvatar())
                .build();
    }
}
