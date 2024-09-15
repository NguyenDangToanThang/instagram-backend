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
    String parentCommentId;
    String parentName;
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
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .parentName(comment.getParentComment() != null ? comment.getParentComment().getUser().getFullname() : null)
                .build();
        if (comment.getReplies() != null) {
            dto.setReplies(comment.getReplies().stream()
                    .map(ReplyCommentResponse::fromEntity)
                    .collect(Collectors.toList()));
            dto.setCountReply(comment.getReplies().size());
        }
        return dto;
    }
}
