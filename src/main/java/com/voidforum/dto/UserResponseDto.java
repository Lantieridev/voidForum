package com.voidforum.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        String id,
        String username,
        String email,
        String displayName,
        String bio,
        boolean notifyLikes,
        boolean notifyComments,
        boolean notifyMentions,
        LocalDateTime createdAt
) {}