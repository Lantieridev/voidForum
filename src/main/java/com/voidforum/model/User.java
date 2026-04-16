package com.voidforum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String role;
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
}