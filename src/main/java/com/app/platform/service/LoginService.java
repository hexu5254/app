package com.app.platform.service;

import com.app.platform.api.dto.LoginRequest;
import com.app.platform.api.dto.UserSessionDto;
import com.app.platform.auth.AuthenticatedContext;
import com.app.platform.config.AuthProperties;
import com.app.platform.domain.AppUser;
import com.app.platform.domain.UserStatus;
import com.app.platform.exception.AuthFailedException;
import com.app.platform.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
public class LoginService {

	private static final Logger log = LoggerFactory.getLogger(LoginService.class);

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthProperties authProperties;
	private final UserLoginPersistence userLoginPersistence;

	public LoginService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
			AuthProperties authProperties, UserLoginPersistence userLoginPersistence) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.authProperties = authProperties;
		this.userLoginPersistence = userLoginPersistence;
	}

	/**
	 * Login pipeline; on any failure throws {@link AuthFailedException} without touching session or last_login_at.
	 */
	public UserSessionDto login(LoginRequest req, HttpServletRequest httpRequest) {
		String loginNameNorm = req.loginName().toLowerCase(Locale.ROOT);

		Optional<AppUser> userOpt = appUserRepository.findByLoginName(loginNameNorm);
		if (userOpt.isEmpty()) {
			log.warn("Login failed: user not found (masked)");
			throw new AuthFailedException();
		}
		AppUser user = userOpt.get();

		UserStatus st = UserStatus.fromCode(user.getStatus());
		if (!st.allowsLogin()) {
			log.warn("Login failed: invalid user status userId={} status={}", user.getId(), user.getStatus());
			throw new AuthFailedException();
		}

		Instant now = Instant.now();
		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(now)) {
			log.warn("Login failed: account locked userId={}", user.getId());
			throw new AuthFailedException();
		}

		if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
			log.warn("Login failed: bad password userId={}", user.getId());
			userLoginPersistence.recordWrongPassword(user);
			throw new AuthFailedException();
		}

		HttpSession old = httpRequest.getSession(false);
		if (old != null) {
			old.invalidate();
		}
		HttpSession session = httpRequest.getSession(true);
		long authenticatedAt = System.currentTimeMillis();
		AuthenticatedContext ctx = new AuthenticatedContext(
				user.getId(),
				user.getLoginName(),
				user.getDisplayName(),
				authenticatedAt);
		session.setAttribute(authProperties.getSessionAttributeName(), ctx);

		userLoginPersistence.recordSuccessfulLogin(user.getId());

		return new UserSessionDto(user.getId(), user.getLoginName(), user.getDisplayName());
	}

	public void logout(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
