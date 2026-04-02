package com.app.platform.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(min = 1, max = 64) @Pattern(regexp = "[a-zA-Z0-9._@-]+", message = "loginName format invalid") String loginName,
		@NotBlank @Size(min = 1, max = 128) String password,
		@Size(max = 128) String displayName) {

	public RegisterRequest {
		loginName = loginName == null ? "" : loginName.trim();
		password = password == null ? "" : password.trim();
		displayName = displayName == null ? null : displayName.trim();
		if (displayName != null && displayName.isEmpty()) {
			displayName = null;
		}
	}
}
