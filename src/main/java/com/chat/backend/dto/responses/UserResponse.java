package com.chat.backend.dto.responses;

public record UserResponse(
        String username,
        String token,
        Integer messageCount
) {
}
