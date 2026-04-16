package com.voidforum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    private String content;
    private String postId;
    private String parentCommentId;  // null = comentario principal, != null = reply
}