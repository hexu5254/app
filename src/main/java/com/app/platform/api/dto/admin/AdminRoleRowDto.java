package com.app.platform.api.dto.admin;

public record AdminRoleRowDto(
		Long id,
		String code,
		String name,
		String status,
		String isViewAll,
		int sequ
) {
}
