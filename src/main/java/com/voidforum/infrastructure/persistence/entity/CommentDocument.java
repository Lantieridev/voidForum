package com.voidforum.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class CommentDocument {
    @Id
    private String id;
    private String content;

    @Indexed
    private String postId;

    private String parentCommentId;
    private String authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
}
