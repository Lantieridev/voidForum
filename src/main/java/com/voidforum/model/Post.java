package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId; // El ID del usuario que lo creó
    private String authorUsername; // Para no tener que buscarlo cada vez
    private List<String> tags;
    private LocalDateTime createdAt;
}