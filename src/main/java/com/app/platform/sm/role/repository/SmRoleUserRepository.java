package com.app.platform.sm.role.repository;

import com.app.platform.sm.role.domain.SmRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmRoleUserRepository extends JpaRepository<SmRoleUser, SmRoleUser.SmRoleUserId> {

	List<SmRoleUser> findByUserId(Long userId);

	void deleteByUserId(Long userId);

	@Query("select distinct ru.userId from SmRoleUser ru where ru.roleId = :roleId")
	List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
}
