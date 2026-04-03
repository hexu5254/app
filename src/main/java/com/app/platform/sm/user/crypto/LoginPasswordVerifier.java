package com.app.platform.sm.user.crypto;

import com.app.platform.sm.user.domain.SmUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * 校验顺序：BCrypt（新项目）→ SHA-256 → MD5（历史兼容）。
 */
public final class LoginPasswordVerifier {

	private LoginPasswordVerifier() {
	}

	public static boolean matches(String plain, SmUser user, PasswordEncoder bcryptEncoder) {
		if (user.getPasswordBcrypt() != null && !user.getPasswordBcrypt().isBlank()) {
			return bcryptEncoder.matches(plain, user.getPasswordBcrypt());
		}
		if (user.getShaPassword() != null && !user.getShaPassword().isBlank()) {
			return Objects.equals(DigestUtil.sha256HexLower(plain), user.getShaPassword().toLowerCase());
		}
		if (user.getPassword() != null && !user.getPassword().isBlank()) {
			return Objects.equals(DigestUtil.md5HexLower(plain), user.getPassword().toLowerCase());
		}
		return false;
	}
}
