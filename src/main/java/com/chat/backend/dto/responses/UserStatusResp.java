package com.chat.backend.dto.responses;

public record UserStatusResp(
        String email,
        Boolean status
) {
}
