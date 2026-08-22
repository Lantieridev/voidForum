package com.voidforum.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "votes")
@CompoundIndex(name = "user_target_type_idx", def = "{'userId': 1, 'targetId': 1, 'targetType': 1}", unique = true)
public class VoteDocument {
    @Id
    private String id;
    private String userId;
    private String targetId;
    private String targetType;
    private int value;
}
