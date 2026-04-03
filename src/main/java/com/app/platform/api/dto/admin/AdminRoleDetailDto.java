package com.app.platform.api.dto.admin;

import java.util.List;

public record AdminRoleDetailDto(
		Long id,
		String code,
		String name,
		String roleDesc,
		String status,
		String isInner,
		String isViewAll,
		int sequ,
		List<Long> assignedOpIds
) {
}
