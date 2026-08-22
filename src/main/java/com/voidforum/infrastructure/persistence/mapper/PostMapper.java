package com.voidforum.infrastructure.persistence.mapper;

import com.voidforum.domain.model.Post;
import com.voidforum.infrastructure.persistence.entity.PostDocument;

import java.util.ArrayList;

public class PostMapper {

    public static Post toDomain(PostDocument entity) {
        if (entity == null) return null;
        return Post.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .authorUsername(entity.getAuthorUsername())
                .authorId(entity.getAuthorId())
                .tags(entity.getTags() != null ? new ArrayList<>(entity.getTags()) : new ArrayList<>())
                .createdAt(entity.getCreatedAt())
                .voteCount(entity.getVoteCount())
                .commentCount(entity.getCommentCount())
                .savedCount(entity.getSavedCount())
                .build();
    }

    public static PostDocument toEntity(Post domain) {
        if (domain == null) return null;
        return PostDocument.builder()
                .id(domain.getId())
                .content(domain.getContent())
                .authorUsername(domain.getAuthorUsername())
                .authorId(domain.getAuthorId())
                .tags(domain.getTags() != null ? new ArrayList<>(domain.getTags()) : new ArrayList<>())
                .createdAt(domain.getCreatedAt())
                .voteCount(domain.getVoteCount())
                .commentCount(domain.getCommentCount())
                .savedCount(domain.getSavedCount())
                .build();
    }
}
