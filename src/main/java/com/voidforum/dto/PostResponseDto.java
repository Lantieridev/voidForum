package com.voidforum.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
        String id,
        String content,
        String authorUsername,
        String authorId,
        List<String> tags,
        int voteCount,
        LocalDateTime createdAt
) {}