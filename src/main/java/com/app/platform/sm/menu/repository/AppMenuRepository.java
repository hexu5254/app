package com.app.platform.sm.menu.repository;

import com.app.platform.sm.menu.domain.AppMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppMenuRepository extends JpaRepository<AppMenu, Long> {

	@Query("select distinct m from AppMenu m left join fetch m.parent "
			+ "where m.clientType = :clientType and m.status = :status order by m.sequ asc, m.id asc")
	List<AppMenu> findWithParentByClientTypeAndStatusOrderBySequAscIdAsc(
			@Param("clientType") String clientType,
			@Param("status") String status);

	List<AppMenu> findByClientTypeAndStatusOrderBySequAsc(String clientType, String status);

	@Query(value = """
			SELECT DISTINCT m.id FROM app_menu m
			INNER JOIN app_op_security aos ON aos.menu_id = m.id AND aos.status = '1'
			INNER JOIN app_op_assign aoa ON aoa.op_id = aos.id
			INNER JOIN sm_role_user sru ON sru.role_id = aoa.role_id
			INNER JOIN app_role ar ON ar.id = sru.role_id AND ar.status = '1'
				AND (ar.validated_start_date IS NULL OR ar.validated_start_date <= CURRENT_DATE)
				AND (ar.validated_end_date IS NULL OR ar.validated_end_date >= CURRENT_DATE)
			WHERE m.client_type = :clientType AND m.status = '1' AND sru.user_id = :userId
			""", nativeQuery = true)
	List<Long> findVisibleMenuIdsForAssignedUser(@Param("userId") long userId, @Param("clientType") String clientType);

	long countByParent_IdAndStatus(long parentId, String status);
}
