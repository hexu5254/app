package com.app.platform.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminCreateRoleRequest(
		@NotBlank @Size(max = 64) @Pattern(regexp = "[a-zA-Z0-9._-]+") String code,
		@NotBlank @Size(max = 128) String name,
		@Size(max = 512) String roleDesc
) {
}
