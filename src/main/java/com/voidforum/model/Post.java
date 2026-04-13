package com.voidforum.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorUsername;
    private List<String> tags;
    private int upvotes;
    private int downvotes;
    private LocalDateTime createdAt;
}