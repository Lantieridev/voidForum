package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String content;
    private String postId;       // Relación con el Post
    private String authorId;     // ID del usuario que comenta
    private String authorUsername;
    private LocalDateTime createdAt;
}