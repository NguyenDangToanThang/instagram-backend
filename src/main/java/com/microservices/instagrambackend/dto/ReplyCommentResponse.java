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
    String id;
    String content;
    Date createdAt;
    String email;
    String userName;
    String parentCommentId;
    String parentName;
    String avatar;

    public static ReplyCommentResponse fromEntity(Comment reply) {
        return ReplyCommentResponse.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .email(reply.getUser().getEmail())
                .userName(reply.getUser().getFullname())
                .avatar(reply.getUser().getAvatar())
                .parentCommentId(reply.getParentComment() != null ? reply.getParentComment().getId() : null)
                .parentName(reply.getParentComment() != null ? reply.getParentComment().getUser().getFullname() : null)
                .build();
    }
}
