package com.app.platform.service;

import com.app.platform.config.AuthProperties;
import com.app.platform.sm.user.domain.SmUser;
import com.app.platform.sm.user.repository.SmUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserLoginPersistence {

	private final SmUserRepository smUserRepository;
	private final AuthProperties authProperties;

	public UserLoginPersistence(SmUserRepository smUserRepository, AuthProperties authProperties) {
		this.smUserRepository = smUserRepository;
		this.authProperties = authProperties;
	}

	@Transactional
	public void recordWrongPassword(SmUser user) {
		SmUser managed = smUserRepository.findById(user.getId()).orElseThrow();
		int next = managed.getFailedLoginCount() + 1;
		managed.setFailedLoginCount(next);
		if (next >= authProperties.getMaxFailedLogins()) {
			managed.setLockedUntil(Instant.now().plusSeconds((long) authProperties.getLockDurationMinutes() * 60));
		}
		smUserRepository.save(managed);
	}

	@Transactional
	public void recordSuccessfulLogin(Long userId) {
		SmUser managed = smUserRepository.findById(userId).orElseThrow();
		managed.setLastLoginTime(Instant.now());
		managed.setFailedLoginCount(0);
		managed.setLockedUntil(null);
		smUserRepository.save(managed);
	}
}
