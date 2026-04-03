package com.app.platform.sm.role.repository;

import com.app.platform.sm.role.domain.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

	List<AppRole> findAllByStatusOrderBySequAscCodeAsc(String status);

	Optional<AppRole> findByCode(String code);

	@Query("select r from AppRole r where r.status = '1' and r.id in "
			+ "(select ru.roleId from SmRoleUser ru where ru.userId = :userId)")
	List<AppRole> findActiveRolesForUser(@Param("userId") Long userId);
}
