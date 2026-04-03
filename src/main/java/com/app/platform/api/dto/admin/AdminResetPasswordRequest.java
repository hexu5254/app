package com.app.platform.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminResetPasswordRequest(
		@NotBlank @Size(min = 8, max = 128) String newPassword,
		Boolean forceChangeOnNextLogin) {

	public AdminResetPasswordRequest {
		newPassword = newPassword == null ? "" : newPassword.trim();
		if (forceChangeOnNextLogin == null) {
			forceChangeOnNextLogin = false;
		}
	}
}
