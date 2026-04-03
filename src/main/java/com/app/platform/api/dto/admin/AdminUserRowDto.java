package com.app.platform.api.dto.admin;

import java.time.Instant;
import java.util.List;

public record AdminUserRowDto(
		long id,
		String code,
		String name,
		String status,
		String userType,
		Long deptId,
		String deptName,
		List<Long> roleIds,
		List<String> roleNames,
		Instant lastLoginTime,
		Instant lockedUntil) {
}
