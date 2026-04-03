package com.app.platform.org.employee.repository;

import com.app.platform.org.employee.domain.SysEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysEmployeeRepository extends JpaRepository<SysEmployee, Long> {
}
