package com.app.platform.auth;

/**
 * Request-scoped identity; must be cleared in a filter {@code finally} block.
 */
public final class AuthContextHolder {

	private static final ThreadLocal<AuthenticatedContext> CTX = new ThreadLocal<>();

	private AuthContextHolder() {
	}

	public static void set(AuthenticatedContext context) {
		CTX.set(context);
	}

	public static AuthenticatedContext get() {
		return CTX.get();
	}

	public static void clear() {
		CTX.remove();
	}
}
