package com.app.platform.auth;

import java.io.Serial;
import java.io.Serializable;

/**
 * Stored in HttpSession under {@link com.app.platform.config.AuthProperties#getSessionAttributeName()}.
 */
public final class AuthenticatedContext implements Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final Long userId;
	private final String loginName;
	private final String displayName;
	private final long authenticatedAtEpochMillis;

	public AuthenticatedContext(Long userId, String loginName, String displayName, long authenticatedAtEpochMillis) {
		this.userId = userId;
		this.loginName = loginName;
		this.displayName = displayName;
		this.authenticatedAtEpochMillis = authenticatedAtEpochMillis;
	}

	public Long getUserId() {
		return userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getAuthenticatedAtEpochMillis() {
		return authenticatedAtEpochMillis;
	}
}
