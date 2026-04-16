package com.voidforum.dto;

public record UpdateProfileDto(
        String username,
        String email,
        String displayName,
        String bio
) {}
