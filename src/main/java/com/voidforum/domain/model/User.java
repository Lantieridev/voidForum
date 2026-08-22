package com.voidforum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String displayName;
    private String bio;

    @Builder.Default
    private boolean notifyLikes = true;

    @Builder.Default
    private boolean notifyComments = true;

    @Builder.Default
    private boolean notifyMentions = true;

    @Builder.Default
    private List<String> followingIds = new ArrayList<>();

    @Builder.Default
    private int followerCount = 0;

    @Builder.Default
    private int followingCount = 0;

    private LocalDateTime createdAt;

    @Builder.Default
    private List<String> savedPosts = new ArrayList<>();
}
