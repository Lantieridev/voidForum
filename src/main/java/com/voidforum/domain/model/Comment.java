package com.voidforum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String content;
    private String postId;
    private String parentCommentId;
    private String authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
}
