package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String content;

    @Indexed // Acelera la carga de hilos de conversación
    private String postId;

    private String authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
}