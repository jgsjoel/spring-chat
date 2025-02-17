package com.chat.backend.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileRequest(
        String message,
        MultipartFile file
) {
}
