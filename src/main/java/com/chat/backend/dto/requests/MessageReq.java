package com.chat.backend.dto.requests;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record MessageReq(
        String sender,
        String sender_token,
        String message,
        String file_url,
        LocalDateTime localDateTime
) { }
