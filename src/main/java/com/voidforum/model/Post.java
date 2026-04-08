package com.voidforum.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
    private List<String> tags;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int upvotes;
    private int downvotes;
}