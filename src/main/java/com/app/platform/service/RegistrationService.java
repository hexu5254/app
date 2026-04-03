package com.app.platform.service;

import com.app.platform.api.dto.RegisterRequest;
import com.app.platform.api.dto.UserSessionDto;
import com.app.platform.core.authentication.Constants;
import com.app.platform.exception.UsernameTakenException;
import com.app.platform.org.employee.domain.SysEmployee;
import com.app.platform.org.employee.repository.SysEmployeeRepository;
import com.app.platform.sm.role.domain.SmRoleUser;
import com.app.platform.sm.role.repository.AppRoleRepository;
import com.app.platform.sm.role.repository.SmRoleUserRepository;
import com.app.platform.sm.user.domain.SmUser;
import com.app.platform.sm.user.repository.SmUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationService {

	private final SmUserRepository smUserRepository;
	private final SysEmployeeRepository sysEmployeeRepository;
	private final AppRoleRepository appRoleRepository;
	private final SmRoleUserRepository smRoleUserRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistrationService(SmUserRepository smUserRepository, SysEmployeeRepository sysEmployeeRepository,
			AppRoleRepository appRoleRepository, SmRoleUserRepository smRoleUserRepository,
			PasswordEncoder passwordEncoder) {
		this.smUserRepository = smUserRepository;
		this.sysEmployeeRepository = sysEmployeeRepository;
		this.appRoleRepository = appRoleRepository;
		this.smRoleUserRepository = smRoleUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 同时写入 sm_user 与 sys_employee，主键一致。
	 */
	@Transactional
	public UserSessionDto register(RegisterRequest request) {
		String codeNorm = request.loginName().toLowerCase(Locale.ROOT);
		if (smUserRepository.existsByCodeIgnoreCase(codeNorm)) {
			throw new UsernameTakenException();
		}
		SmUser sm = new SmUser();
		sm.setCode(codeNorm);
		sm.setName(request.displayName());
		sm.setPasswordBcrypt(passwordEncoder.encode(request.password()));
		sm.setUserType((short) 0);
		sm.setStatus(Constants.USER_STATUS_NORMAL);
		sm.setFailedLoginCount(0);
		smUserRepository.save(sm);

		SysEmployee emp = new SysEmployee();
		emp.setSmUser(sm);
		emp.setStatus(Constants.USER_STATUS_NORMAL);
		emp.setCode(codeNorm);
		emp.setName(request.displayName());
		emp.setUserId(sm.getId());
		sysEmployeeRepository.save(emp);

		appRoleRepository.findByCode(Constants.APP_ROLE_CODE_NORMAL_USER)
				.filter(r -> "1".equals(r.getStatus()))
				.ifPresent(role -> {
					SmRoleUser link = new SmRoleUser();
					link.setUserId(sm.getId());
					link.setRoleId(role.getId());
					smRoleUserRepository.save(link);
				});

		return new UserSessionDto(sm.getId(), sm.getCode(), sm.getName());
	}
}
