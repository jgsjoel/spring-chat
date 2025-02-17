package com.chat.backend.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUserUtil {

    public String getAuthenticatedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

}
