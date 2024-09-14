package com.microservices.instagrambackend.web.rest;

import com.microservices.instagrambackend.dto.FollowRequest;
import com.microservices.instagrambackend.dto.FollowerResponse;
import com.microservices.instagrambackend.dto.ResponseObject;
import com.microservices.instagrambackend.service.FollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowResource {

    FollowService followService;

    @PostMapping
    public ResponseEntity<ResponseObject<?>> followUser(@RequestBody FollowRequest request) {
        followService.followUser(request.email());
        return new ResponseEntity<>(ResponseObject.success(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<FollowerResponse>>> getListOfFollowedUsers() {
        return new ResponseEntity<>(ResponseObject.success(followService.followUsers()), HttpStatus.OK);
    }
}
