package com.voidforum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    private String id;
    private String userId;
    private String targetId;
    private String targetType;
    private int value;
}
