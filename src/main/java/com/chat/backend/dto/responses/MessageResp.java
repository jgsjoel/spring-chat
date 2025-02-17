package com.chat.backend.dto.responses;

import java.time.LocalDateTime;

public record MessageResp(
        String sender,
        String sender_token,
        String message,
        LocalDateTime time
) {
}
