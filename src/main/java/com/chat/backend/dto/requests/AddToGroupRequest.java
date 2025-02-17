package com.chat.backend.dto.requests;

import jakarta.validation.constraints.NotEmpty;

public record AddToGroupRequest(
        @NotEmpty(message = "Invalid User")
        String user_token,
        @NotEmpty(message = "Invalid Channel")
        String channel_token
) {
}
