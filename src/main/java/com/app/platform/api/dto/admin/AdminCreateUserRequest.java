package com.app.platform.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Locale;

public record AdminCreateUserRequest(
		@NotBlank @Size(min = 1, max = 64) @Pattern(regexp = "[a-zA-Z0-9._@-]+", message = "code format invalid") String code,
		@NotBlank @Size(min = 8, max = 128) String password,
		@Size(max = 128) String displayName,
		String userType,
		@NotBlank @Size(min = 1, max = 1) String status,
		Long deptId,
		List<Long> roleIds) {

	public AdminCreateUserRequest {
		code = code == null ? "" : code.trim().toLowerCase(Locale.ROOT);
		password = password == null ? "" : password.trim();
		displayName = displayName == null ? null : displayName.trim();
		if (displayName != null && displayName.isEmpty()) {
			displayName = null;
		}
		if (userType == null || userType.isBlank()) {
			userType = "0";
		}
		if (roleIds == null) {
			roleIds = List.of();
		}
	}
}
