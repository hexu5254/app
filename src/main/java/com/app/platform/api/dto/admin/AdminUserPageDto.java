package com.app.platform.api.dto.admin;

import java.util.List;

public record AdminUserPageDto(
		List<AdminUserRowDto> content,
		long totalElements,
		int totalPages,
		int number,
		int size) {
}
