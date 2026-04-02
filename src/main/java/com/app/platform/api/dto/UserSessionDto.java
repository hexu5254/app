package com.app.platform.api.dto;

public record UserSessionDto(long userId, String loginName, String displayName) {
}
