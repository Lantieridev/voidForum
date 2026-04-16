package com.voidforum.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
        String id,
        String content,
        String authorUsername,
        String postId,
        LocalDateTime createdAt
) {}