package com.app.platform.domain;

import java.util.Arrays;

/**
 * app_user.status — fixed enum per specification.
 */
public enum UserStatus {

	DELETED((short) 0),
	NORMAL((short) 1),
	DISABLED((short) 2),
	FROZEN((short) 3),
	MUST_CHANGE_PASSWORD((short) 4);

	private final short code;

	UserStatus(short code) {
		this.code = code;
	}

	public short getCode() {
		return code;
	}

	public static UserStatus fromCode(short code) {
		return Arrays.stream(values())
				.filter(s -> s.code == code)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown user status: " + code));
	}

	/** MVP: only NORMAL may complete login. */
	public boolean allowsLogin() {
		return this == NORMAL;
	}
}
