package com.app.platform.org.employee.service;

import com.app.platform.org.employee.domain.SysEmployee;
import com.app.platform.org.employee.repository.SysEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysEmployeeService {

	private final SysEmployeeRepository sysEmployeeRepository;

	public SysEmployeeService(SysEmployeeRepository sysEmployeeRepository) {
		this.sysEmployeeRepository = sysEmployeeRepository;
	}

	public Optional<SysEmployee> getById(Long id) {
		return sysEmployeeRepository.findById(id);
	}
}
