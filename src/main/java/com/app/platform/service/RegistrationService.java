package com.app.platform.service;

import com.app.platform.api.dto.RegisterRequest;
import com.app.platform.api.dto.UserSessionDto;
import com.app.platform.domain.AppUser;
import com.app.platform.domain.UserStatus;
import com.app.platform.exception.UsernameTakenException;
import com.app.platform.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistrationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Creates a new normal user; does not establish a session (client calls login).
	 */
	@Transactional
	public UserSessionDto register(RegisterRequest request) {
		String loginNameNorm = request.loginName().toLowerCase(Locale.ROOT);
		if (appUserRepository.existsByLoginName(loginNameNorm)) {
			throw new UsernameTakenException();
		}
		AppUser user = new AppUser();
		user.setLoginName(loginNameNorm);
		user.setDisplayName(request.displayName());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setStatus(UserStatus.NORMAL.getCode());
		user.setFailedLoginCount(0);
		appUserRepository.save(user);
		return new UserSessionDto(user.getId(), user.getLoginName(), user.getDisplayName());
	}
}
