package com.chat.backend.dto.requests;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "Enter Email")
        String email,
        @NotEmpty(message = "Enter Password")
        String password
) {
}
