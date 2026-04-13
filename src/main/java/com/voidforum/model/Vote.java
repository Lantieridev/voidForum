package com.voidforum.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "votes")
public class Vote {
    @Id private String id;
    private String userId;
    private String targetId; // ID del post o comentario votado
    private String targetType; // "post" o "comment"
    private String voteType; // "up" o "down"
}