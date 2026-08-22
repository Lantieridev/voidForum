package com.voidforum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private String id;
    private String content;
    private String authorUsername;
    private String authorId;
    private List<String> tags;
    private LocalDateTime createdAt;

    @Builder.Default
    private Integer voteCount = 0;

    @Builder.Default
    private Integer commentCount = 0;

    @Builder.Default
    private Integer savedCount = 0;
}
