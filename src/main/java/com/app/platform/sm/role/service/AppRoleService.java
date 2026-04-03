package com.app.platform.sm.role.service;

import com.app.platform.core.authentication.RoleSnapshot;
import com.app.platform.sm.role.domain.AppRole;
import com.app.platform.sm.role.repository.AppRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppRoleService {

	private final AppRoleRepository appRoleRepository;

	public AppRoleService(AppRoleRepository appRoleRepository) {
		this.appRoleRepository = appRoleRepository;
	}

	public List<RoleSnapshot> getUserRoles(Long userId) {
		return appRoleRepository.findActiveRolesForUser(userId).stream()
				.map(this::toSnapshot)
				.toList();
	}

	private RoleSnapshot toSnapshot(AppRole r) {
		return new RoleSnapshot(r.getId(), r.getCode(), r.getName());
	}
}
