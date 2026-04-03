package com.app.platform.api.dto.admin;

import java.util.List;

public record AdminReplaceRolesRequest(List<Long> roleIds) {

	public AdminReplaceRolesRequest {
		if (roleIds == null) {
			roleIds = List.of();
		}
	}
}
