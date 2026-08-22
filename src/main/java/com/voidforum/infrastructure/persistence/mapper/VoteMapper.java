package com.voidforum.infrastructure.persistence.mapper;

import com.voidforum.domain.model.Vote;
import com.voidforum.infrastructure.persistence.entity.VoteDocument;

public class VoteMapper {

    public static Vote toDomain(VoteDocument entity) {
        if (entity == null) return null;
        return Vote.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .targetId(entity.getTargetId())
                .targetType(entity.getTargetType())
                .value(entity.getValue())
                .build();
    }

    public static VoteDocument toEntity(Vote domain) {
        if (domain == null) return null;
        return VoteDocument.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .targetId(domain.getTargetId())
                .targetType(domain.getTargetType())
                .value(domain.getValue())
                .build();
    }
}
