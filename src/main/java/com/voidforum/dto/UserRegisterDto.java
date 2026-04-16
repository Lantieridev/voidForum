package com.voidforum.dto;

public record UserRegisterDto(
        String username,
        String email,
        String password
) {}