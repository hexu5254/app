package com.app.platform.sm.menu.repository;

import com.app.platform.sm.menu.domain.AppOpAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppOpAssignRepository extends JpaRepository<AppOpAssign, Long> {

	List<AppOpAssign> findByRole_Id(Long roleId);

	@Modifying(clearAutomatically = true)
	@Query("delete from AppOpAssign a where a.role.id = :roleId")
	void deleteByRole_Id(@Param("roleId") long roleId);

	@Modifying(clearAutomatically = true)
	@Query("delete from AppOpAssign a where a.role.id = :roleId and a.op.menu.clientType = :clientType")
	void deleteByRoleIdAndMenuClientType(@Param("roleId") long roleId, @Param("clientType") String clientType);

	@Query("select o.id from AppOpAssign a join a.op o join o.menu m where a.role.id = :roleId and m.clientType = :clientType")
	List<Long> findOpIdsByRoleIdAndMenuClientType(@Param("roleId") long roleId, @Param("clientType") String clientType);
}
