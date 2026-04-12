package com.voidforum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    private String title;
    private String content;

    @Indexed
    private String authorId;

    private String authorUsername;

    @Indexed
    @Builder.Default // <--- Clave para evitar NullPointerExceptions
    private List<String> tags = new ArrayList<>();

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Nombres ajustados para matchear exactamente con el API.md
    private int upvotes;
    private int downvotes;
    private int commentsCount;
}