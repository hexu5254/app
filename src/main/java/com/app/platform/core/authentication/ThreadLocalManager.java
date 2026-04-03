package com.app.platform.core.authentication;

import com.app.platform.core.authentication.intf.IUser;

public final class ThreadLocalManager {

	private static final ThreadLocal<IUser> USER_LOCAL = new ThreadLocal<>();

	private ThreadLocalManager() {
	}

	public static void setUserLocal(IUser user) {
		USER_LOCAL.set(user);
	}

	public static IUser getUserLocal() {
		return USER_LOCAL.get();
	}

	public static void clearUserLocal() {
		USER_LOCAL.remove();
	}
}
