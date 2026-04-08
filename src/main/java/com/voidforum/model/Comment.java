package com.voidforum.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "comments")
@CompoundIndex(name = "post_created", def = "{'postId': 1, 'createdAt': -1}")
public class Comment {
    @Id
    private String id;

    @Indexed
    private String postId;

    @Indexed
    private String authorId;

    private String authorUsername;

    private String content;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int upvotes;
    private int downvotes;
}