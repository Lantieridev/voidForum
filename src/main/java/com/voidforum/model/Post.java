package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed; // <--- Importante
import org.springframework.data.mongodb.core.index.TextIndexed; // <--- Para búsquedas pro
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @TextIndexed(weight = 1)
    private String content;

    @Indexed
    private String authorUsername;

    private String authorId;

    @Indexed
    private List<String> tags;

    private LocalDateTime createdAt;

    @Builder.Default
    private Integer voteCount = 0;

    @Builder.Default
    private Integer commentCount = 0;
}