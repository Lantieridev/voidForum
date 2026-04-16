package com.voidforum.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponseDto(
        String id,
        String content,
        String authorUsername,
        String postId,
        String parentCommentId,
        LocalDateTime createdAt,
        int voteCount,
        int userVote,
        List<CommentResponseDto> replies
) {}