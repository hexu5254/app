package com.app.platform.auth;

import com.app.platform.core.authentication.intf.IUser;

import java.util.List;

public final class AdminAuthSupport {

	private AdminAuthSupport() {
	}

	/**
	 * 平台管理员：{@code user_type=9}、{@code IS_ADMIN_EMP}（含 {@code super_admin} 应用角色）、或配置 {@code adminUserIds}。
	 */
	public static boolean isPlatformAdmin(IUser user, List<Long> adminUserIds) {
		if (user == null || user.getLoginUserId() == null) {
			return false;
		}
		if (adminUserIds != null && adminUserIds.contains(user.getLoginUserId())) {
			return true;
		}
		return user.isAdmin();
	}
}
