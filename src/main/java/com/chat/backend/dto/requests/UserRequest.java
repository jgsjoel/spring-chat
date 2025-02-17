package com.chat.backend.dto.requests;

import com.chat.backend.dto.requests.groups.Create;
import com.chat.backend.dto.requests.groups.Update;
import jakarta.validation.constraints.NotEmpty;

public record UserRequest(
        @NotEmpty(message = "Enter user name",groups = {Create.class})
        String user_name,
        @NotEmpty(message = "Enter email",groups = {Create.class})
        String email,
        @NotEmpty(message = "Enter password",groups = {Create.class, Update.class})
        String password
) {
}
