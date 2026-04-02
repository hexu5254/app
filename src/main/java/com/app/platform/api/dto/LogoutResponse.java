package com.app.platform.api.dto;

public record LogoutResponse(boolean success) {

	public static LogoutResponse ok() {
		return new LogoutResponse(true);
	}
}
