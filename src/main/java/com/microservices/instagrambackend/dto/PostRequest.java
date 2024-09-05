package com.microservices.instagrambackend.dto;

import org.springframework.web.multipart.MultipartRequest;

public record PostRequest(MultipartRequest image, String caption) {
}
