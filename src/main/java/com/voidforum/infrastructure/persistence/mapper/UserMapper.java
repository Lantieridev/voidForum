package com.voidforum.infrastructure.persistence.mapper;

import com.voidforum.domain.model.User;
import com.voidforum.infrastructure.persistence.entity.UserDocument;

import java.util.ArrayList;

public class UserMapper {

    public static User toDomain(UserDocument entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .displayName(entity.getDisplayName())
                .bio(entity.getBio())
                .notifyLikes(entity.isNotifyLikes())
                .notifyComments(entity.isNotifyComments())
                .notifyMentions(entity.isNotifyMentions())
                .followingIds(entity.getFollowingIds() != null ? new ArrayList<>(entity.getFollowingIds()) : new ArrayList<>())
                .followerCount(entity.getFollowerCount())
                .followingCount(entity.getFollowingCount())
                .createdAt(entity.getCreatedAt())
                .savedPosts(entity.getSavedPosts() != null ? new ArrayList<>(entity.getSavedPosts()) : new ArrayList<>())
                .build();
    }

    public static UserDocument toEntity(User domain) {
        if (domain == null) return null;
        return UserDocument.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .displayName(domain.getDisplayName())
                .bio(domain.getBio())
                .notifyLikes(domain.isNotifyLikes())
                .notifyComments(domain.isNotifyComments())
                .notifyMentions(domain.isNotifyMentions())
                .followingIds(domain.getFollowingIds() != null ? new ArrayList<>(domain.getFollowingIds()) : new ArrayList<>())
                .followerCount(domain.getFollowerCount())
                .followingCount(domain.getFollowingCount())
                .createdAt(domain.getCreatedAt())
                .savedPosts(domain.getSavedPosts() != null ? new ArrayList<>(domain.getSavedPosts()) : new ArrayList<>())
                .build();
    }
}
