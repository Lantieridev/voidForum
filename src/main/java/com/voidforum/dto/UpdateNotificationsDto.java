package com.voidforum.dto;

public record UpdateNotificationsDto(
        boolean notifyLikes,
        boolean notifyComments,
        boolean notifyMentions
) {}
