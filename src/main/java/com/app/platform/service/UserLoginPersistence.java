package com.app.platform.service;

import com.app.platform.config.AuthProperties;
import com.app.platform.domain.AppUser;
import com.app.platform.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserLoginPersistence {

	private final AppUserRepository appUserRepository;
	private final AuthProperties authProperties;

	public UserLoginPersistence(AppUserRepository appUserRepository, AuthProperties authProperties) {
		this.appUserRepository = appUserRepository;
		this.authProperties = authProperties;
	}

	@Transactional
	public void recordWrongPassword(AppUser user) {
		AppUser managed = appUserRepository.findById(user.getId()).orElseThrow();
		int next = managed.getFailedLoginCount() + 1;
		managed.setFailedLoginCount(next);
		if (next >= authProperties.getMaxFailedLogins()) {
			managed.setLockedUntil(Instant.now().plusSeconds((long) authProperties.getLockDurationMinutes() * 60));
		}
		appUserRepository.save(managed);
	}

	@Transactional
	public void recordSuccessfulLogin(Long userId) {
		AppUser managed = appUserRepository.findById(userId).orElseThrow();
		managed.setLastLoginAt(Instant.now());
		managed.setFailedLoginCount(0);
		managed.setLockedUntil(null);
		appUserRepository.save(managed);
	}
}
