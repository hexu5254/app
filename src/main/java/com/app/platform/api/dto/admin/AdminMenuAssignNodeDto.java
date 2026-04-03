package com.app.platform.api.dto.admin;

import java.util.List;

public record AdminMenuAssignNodeDto(
		long id,
		Long parentId,
		String name,
		int sequ,
		List<AdminAssignTreeOpDto> operations,
		List<AdminMenuAssignNodeDto> children
) {
}
