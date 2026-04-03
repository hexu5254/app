package com.app.platform.sm.menu.repository;

import com.app.platform.sm.menu.domain.AppOpSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppOpSecurityRepository extends JpaRepository<AppOpSecurity, Long> {

	@Query("""
			select o from AppOpSecurity o join fetch o.menu m
			where o.status = '1' and m.status = '1' and m.clientType = :clientType
				and o.groupId is null
			order by m.sequ asc, m.id asc, o.sequ asc, o.code asc
			""")
	List<AppOpSecurity> findSelectableOpsByClientType(@Param("clientType") String clientType);

	@Query(value = """
			SELECT DISTINCT aos.code FROM app_op_security aos
			WHERE aos.menu_id = :menuId AND aos.group_id IS NULL AND aos.status = '1'
			""", nativeQuery = true)
	List<String> findAllActiveOpCodesForMenu(@Param("menuId") long menuId);

	@Query(value = """
			SELECT DISTINCT aos.code FROM app_op_security aos
			INNER JOIN app_op_assign aoa ON aoa.op_id = aos.id
			INNER JOIN sm_role_user sru ON sru.role_id = aoa.role_id
			INNER JOIN app_role ar ON ar.id = sru.role_id AND ar.status = '1'
				AND (ar.validated_start_date IS NULL OR ar.validated_start_date <= CURRENT_DATE)
				AND (ar.validated_end_date IS NULL OR ar.validated_end_date >= CURRENT_DATE)
			WHERE aos.menu_id = :menuId AND aos.group_id IS NULL AND aos.status = '1'
				AND sru.user_id = :userId
			""", nativeQuery = true)
	List<String> findAssignedOpCodesForUserAndMenu(@Param("userId") long userId, @Param("menuId") long menuId);
}
