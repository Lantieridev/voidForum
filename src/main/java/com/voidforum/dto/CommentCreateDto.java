package com.voidforum.dto;

public record CommentCreateDto(
        String content,
        String postId
) {}