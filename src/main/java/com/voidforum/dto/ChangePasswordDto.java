package com.voidforum.dto;

public record ChangePasswordDto(
        String currentPassword,
        String newPassword
) {}
