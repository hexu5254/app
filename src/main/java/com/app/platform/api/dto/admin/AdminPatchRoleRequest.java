package com.app.platform.api.dto.admin;

import jakarta.validation.constraints.Size;

public record AdminPatchRoleRequest(
		@Size(max = 128) String name,
		@Size(max = 512) String roleDesc,
		String status,
		String isViewAll,
		Integer sequ
) {
}
