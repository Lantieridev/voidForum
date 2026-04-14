package com.voidforum.dto;

import java.util.List;

public record PostCreateDto(
        String title,
        String content,
        List<String> tags
) {}