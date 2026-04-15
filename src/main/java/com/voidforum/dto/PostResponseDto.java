package com.voidforum.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
        String id,
        String title,
        String content,
        String authorUsername,
        List<String> tags,
        int voteCount, // <--- Nuevo campo
        LocalDateTime createdAt
) {}