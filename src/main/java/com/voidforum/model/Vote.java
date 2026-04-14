package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "votes")
public class Vote {
    @Id
    private String id;
    private String userId;
    private String targetId; // ID del Post o Comentario votado
    private int value;       // 1 para Upvote, -1 para Downvote
}