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

    @TextIndexed(weight = 2) // Índice de texto para el buscador
    private String title;

    @TextIndexed(weight = 1) // Índice de texto para el buscador
    private String content;

    @Indexed // Acelera filtrar por autor
    private String authorUsername;

    private String authorId;

    @Indexed // Acelera filtrar por categorías/tags
    private List<String> tags;

    private LocalDateTime createdAt;
}