package com.medibridge.prescription_service_medibridge.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName(); // In our filter, principal is the userId
        }
        throw new IllegalStateException("No authenticated user");
    }
}
