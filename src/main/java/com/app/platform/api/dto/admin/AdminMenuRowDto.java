package com.app.platform.api.dto.admin;

public record AdminMenuRowDto(
		long id,
		Long parentId,
		String name,
		String path,
		String icon,
		String menuType,
		String clientType,
		int sequ,
		String status,
		String controlType
) {
}
