package com.voidforum.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    @Id private String id;
    private String postId;
    private String authorId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;
}