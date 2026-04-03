package com.app.platform.admin;

import com.app.platform.core.authentication.Constants;
import com.app.platform.org.employee.domain.SysEmployee;
import com.app.platform.sm.role.domain.SmRoleUser;
import com.app.platform.sm.user.domain.SmUser;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SmUserSpecifications {

	private SmUserSpecifications() {
	}

	public static Specification<SmUser> keywordLike(String keyword) {
		return (root, q, cb) -> {
			if (keyword == null || keyword.isBlank()) {
				return cb.conjunction();
			}
			String pattern = "%" + keyword.trim().toLowerCase() + "%";
			return cb.or(
					cb.like(cb.lower(root.get("code")), pattern),
					cb.like(cb.lower(root.get("name")), pattern));
		};
	}

	/**
	 * @param statusCsv {@code null} 或空白：排除已删除（{@code status != '0'}）；否则按逗号解析多状态。
	 */
	public static Specification<SmUser> statusFilter(String statusCsv) {
		return (root, q, cb) -> {
			if (statusCsv == null || statusCsv.isBlank()) {
				return cb.notEqual(root.get("status"), Constants.USER_STATUS_DELETED);
			}
			List<String> list = Arrays.stream(statusCsv.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toList());
			if (list.isEmpty()) {
				return cb.notEqual(root.get("status"), Constants.USER_STATUS_DELETED);
			}
			return root.get("status").in(list);
		};
	}

	public static Specification<SmUser> hasRole(Long roleId) {
		return (root, q, cb) -> {
			if (roleId == null) {
				return cb.conjunction();
			}
			Subquery<Long> sq = q.subquery(Long.class);
			Root<SmRoleUser> ru = sq.from(SmRoleUser.class);
			sq.select(ru.get("userId")).where(cb.equal(ru.get("roleId"), roleId));
			return root.get("id").in(sq);
		};
	}

	public static Specification<SmUser> deptIdEq(Long deptId) {
		return (root, q, cb) -> {
			if (deptId == null) {
				return cb.conjunction();
			}
			Subquery<Long> sq = q.subquery(Long.class);
			Root<SysEmployee> er = sq.from(SysEmployee.class);
			sq.select(er.get("id")).where(cb.equal(er.get("deptId"), deptId));
			return root.get("id").in(sq);
		};
	}
}
