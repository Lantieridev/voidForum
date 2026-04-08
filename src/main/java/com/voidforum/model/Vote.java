package com.voidforum.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "votes")
@CompoundIndex(name = "user_target", def = "{'userId': 1, 'targetId': 1}", unique = true)
public class Vote {
    @Id
    private String id;

    private String userId;
    private String targetId;
    private String targetType; // "post" or "comment"
    private String voteType;   // "up" or "down"
    private LocalDateTime createdAt;
}