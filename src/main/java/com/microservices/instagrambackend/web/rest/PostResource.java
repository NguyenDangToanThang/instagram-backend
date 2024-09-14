package com.microservices.instagrambackend.web.rest;

import com.microservices.instagrambackend.dto.*;
import com.microservices.instagrambackend.service.CommentService;
import com.microservices.instagrambackend.service.LikeService;
import com.microservices.instagrambackend.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostResource {

    PostService postService;
    LikeService likeService;
    CommentService commentService;

    @GetMapping()
    public ResponseEntity<ResponseObject<?>> getAllPost(
            @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable) {
        Page<PostResponse> responses = postService.getAllPost(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", responses.getContent());
        response.put("currentPage", responses.getNumber());
        response.put("totalItems", responses.getTotalElements());
        response.put("totalPages", responses.getTotalPages());

        return new ResponseEntity<>(ResponseObject.success(response), HttpStatus.OK);
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<ResponseObject<?>> createPost (@RequestPart(value = "image", required = false) MultipartFile image,
                                                        @RequestPart("caption") String caption) {
        postService.createPost(image,caption);
        return new ResponseEntity<>(ResponseObject.success(), HttpStatus.OK);
    }

    @PostMapping("/like-post")
    public ResponseEntity<ResponseObject<Integer>> likePost(@RequestBody LikePostRequest request) {
        log.info("Request Like Post: {}", request);
        return new ResponseEntity<>(ResponseObject.success(
                likeService.likePost(request.postId())), HttpStatus.OK);
    }

    @PostMapping("/get-lists-like-post")
    public ResponseEntity<ResponseObject<List<ListLikeResponse>>> getListsLikePost(@RequestBody LikePostRequest request) {
        log.info("Request Get Lists Like Post: {}", request);
        return new ResponseEntity<>(ResponseObject.success(
                likeService.getListsLike(request)), HttpStatus.OK);
    }

    @PostMapping("/comment-post")
    public ResponseEntity<ResponseObject<Integer>> commentPost(@RequestBody CommentPostRequest request) {
        log.info("Request Comment Post: {}", request);
        return new ResponseEntity<>(ResponseObject.success(
                commentService.commentPost(request)), HttpStatus.OK);
    }

    @PostMapping("/reply-comment/{commentId}")
    public ResponseEntity<ResponseObject<?>> replyPost(@PathVariable String commentId,
                                                       @RequestBody CommentPostRequest request) {

        return new ResponseEntity<>(ResponseObject.success(
                commentService
                        .createReply(commentId, request)), HttpStatus.OK);
    }

    @PostMapping("/get-lists-comment-post")
    public ResponseEntity<ResponseObject<List<ListCommentResponse>>> getListsCommentPost(@RequestBody LikePostRequest request) {
        log.info("Request Get Lists Comment Post: {}", request);
        return new ResponseEntity<>(ResponseObject.success(
                commentService
                        .getListsComment(request)), HttpStatus.OK);
    }

    @GetMapping("/get-comment-replies/{commentId}")
    public ResponseEntity<ResponseObject<?>> getCommentReplyPost(@PathVariable String commentId) {
        return new ResponseEntity<>(ResponseObject.success(
                commentService
                        .getCommentWithReplies(commentId)
                        .getReplies()), HttpStatus.OK);
    }
}
