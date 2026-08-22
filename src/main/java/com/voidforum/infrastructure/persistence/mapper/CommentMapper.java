package com.voidforum.infrastructure.persistence.mapper;

import com.voidforum.domain.model.Comment;
import com.voidforum.infrastructure.persistence.entity.CommentDocument;

public class CommentMapper {

    public static Comment toDomain(CommentDocument entity) {
        if (entity == null) return null;
        return Comment.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .postId(entity.getPostId())
                .parentCommentId(entity.getParentCommentId())
                .authorId(entity.getAuthorId())
                .authorUsername(entity.getAuthorUsername())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static CommentDocument toEntity(Comment domain) {
        if (domain == null) return null;
        return CommentDocument.builder()
                .id(domain.getId())
                .content(domain.getContent())
                .postId(domain.getPostId())
                .parentCommentId(domain.getParentCommentId())
                .authorId(domain.getAuthorId())
                .authorUsername(domain.getAuthorUsername())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
