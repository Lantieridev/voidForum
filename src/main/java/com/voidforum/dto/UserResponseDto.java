package com.voidforum.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        String id,
        String username,
        String email,
        LocalDateTime createdAt
) {}