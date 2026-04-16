package com.voidforum.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex; // <--- Nuevo
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "votes")
// Esto evita que un usuario vote dos veces el mismo post a nivel base de datos
@CompoundIndex(name = "user_target_type_idx", def = "{'userId': 1, 'targetId': 1, 'targetType': 1}", unique = true)
public class Vote {
    @Id
    private String id;
    private String userId;
    private String targetId;
    private String targetType;  // "post" o "comment"
    private int value;  // 1 = like, -1 = dislike
}