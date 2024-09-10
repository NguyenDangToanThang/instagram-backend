package com.microservices.instagrambackend.dto;

import org.springframework.web.multipart.MultipartFile;

public record PostRequest(MultipartFile image, String caption) {
}
