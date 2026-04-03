package com.app.platform.service;

import com.app.platform.api.dto.LoginRequest;
import com.app.platform.api.dto.UserSessionDto;
import com.app.platform.core.authentication.Constants;
import com.app.platform.core.authentication.UserManager;
import com.app.platform.core.authentication.intf.IUser;
import com.app.platform.exception.AuthFailedException;
import com.app.platform.sm.user.crypto.LoginPasswordVerifier;
import com.app.platform.sm.user.domain.SmUser;
import com.app.platform.sm.user.repository.SmUserRepository;
import com.app.platform.sm.user.service.IUserService;
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

	private final SmUserRepository smUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserLoginPersistence userLoginPersistence;
	private final IUserService userService;

	public LoginService(SmUserRepository smUserRepository, PasswordEncoder passwordEncoder,
			UserLoginPersistence userLoginPersistence, IUserService userService) {
		this.smUserRepository = smUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.userLoginPersistence = userLoginPersistence;
		this.userService = userService;
	}

	/**
	 * 登录：校验 sm_user；成功后写入 Session（{@link Constants#SESSION_USER}）与 ThreadLocal。
	 */
	public UserSessionDto login(LoginRequest req, HttpServletRequest httpRequest) {
		String codeNorm = req.loginName().toLowerCase(Locale.ROOT);

		Optional<SmUser> userOpt = smUserRepository.findByCodeIgnoreCase(codeNorm);
		if (userOpt.isEmpty()) {
			log.warn("Login failed: user not found (masked)");
			throw new AuthFailedException();
		}
		SmUser account = userOpt.get();

		if (Constants.USER_STATUS_DELETED.equals(account.getStatus())) {
			log.warn("Login failed: deleted user userId={}", account.getId());
			throw new AuthFailedException();
		}
		if (!Constants.USER_STATUS_NORMAL.equals(account.getStatus())) {
			log.warn("Login failed: invalid user status userId={} status={}", account.getId(), account.getStatus());
			throw new AuthFailedException();
		}

		Instant now = Instant.now();
		if (account.getLockedUntil() != null && account.getLockedUntil().isAfter(now)) {
			log.warn("Login failed: account locked userId={}", account.getId());
			throw new AuthFailedException();
		}

		if (!LoginPasswordVerifier.matches(req.password(), account, passwordEncoder)) {
			log.warn("Login failed: bad password userId={}", account.getId());
			userLoginPersistence.recordWrongPassword(account);
			throw new AuthFailedException();
		}

		HttpSession old = httpRequest.getSession(false);
		if (old != null) {
			old.invalidate();
		}
		httpRequest.getSession(true);

		IUser user = userService.getById(account.getId());
		UserManager.setLoginUser(user, Constants.LOGIN_TYPE_WEB, httpRequest, true);

		return new UserSessionDto(account.getId(), account.getCode(), user.getDisplayName());
	}

	public void logout(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
