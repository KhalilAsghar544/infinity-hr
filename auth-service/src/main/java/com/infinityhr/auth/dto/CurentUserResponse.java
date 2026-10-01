package com.infinityhr.auth.dto;

import java.time.Instant;
import java.util.List;

public record CurentUserResponse(
        String userId,
        String username,
        String employeeId,
        List<String> permissons,
        Instant tokenExpiresAt
) {
}
