package com.voidforum.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class PostDocument {
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

    @Builder.Default
    private Integer savedCount = 0;
}
