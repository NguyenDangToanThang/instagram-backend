package com.microservices.instagrambackend.dto;

import com.microservices.instagrambackend.domain.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ListCommentResponse {
    String id;
    String username;
    String email;
    String avatar;
    Date createdDate;
    String content;
    int countReply;
    List<ReplyCommentResponse> replies;

    public static ListCommentResponse fromEntity(Comment comment) {
        ListCommentResponse dto = ListCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedAt())
                .email(comment.getUser().getEmail())
                .username(comment.getUser().getFullname())
                .avatar(comment.getUser().getAvatar())
                .build();
        if (comment.getReplies() != null) {
            dto.setReplies(comment.getReplies().stream()
                    .map(ReplyCommentResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
