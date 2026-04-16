package com.voidforum.dto;

import java.time.LocalDateTime;

public record UserProfileDto(
        String id,
        String username,
        String displayName,
        String bio,
        int followerCount,
        int followingCount,
        boolean isFollowing,
        LocalDateTime createdAt
) {}
