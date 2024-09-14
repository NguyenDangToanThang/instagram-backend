package com.microservices.instagrambackend.service;

import com.microservices.instagrambackend.domain.Comment;
import com.microservices.instagrambackend.dto.CommentPostRequest;
import com.microservices.instagrambackend.dto.LikePostRequest;
import com.microservices.instagrambackend.dto.ListCommentResponse;
import com.microservices.instagrambackend.dto.ReplyCommentResponse;

import java.util.List;

public interface CommentService {
    int commentPost(CommentPostRequest request);
    List<ListCommentResponse> getListsComment(LikePostRequest request);
    ListCommentResponse getCommentWithReplies(String commentId);
    ReplyCommentResponse createReply(String parentCommentId, CommentPostRequest reply);
}
