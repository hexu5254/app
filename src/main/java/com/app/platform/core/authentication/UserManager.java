package com.app.platform.core.authentication;

import com.app.platform.core.authentication.impl.User;
import com.app.platform.core.authentication.intf.IUser;
import com.app.platform.permission.OperationPermissionEvaluator;
import com.app.platform.sm.user.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

/**
 * 门面：ThreadLocal / Session 取当前用户；写会话委托 {@code userServiceImpl}；操作码委托 {@link OperationPermissionEvaluator}。
 */
@Component
public class UserManager {

	private static UserManager instance;

	private final IUserService userService;
	private final OperationPermissionEvaluator operationPermissionEvaluator;

	public UserManager(IUserService userService, OperationPermissionEvaluator operationPermissionEvaluator) {
		this.userService = userService;
		this.operationPermissionEvaluator = operationPermissionEvaluator;
	}

	@PostConstruct
	void registerStatic() {
		instance = this;
	}

	public static void setLoginUser(IUser user, String loginType, HttpServletRequest request, boolean recordLog) {
		instance.userService.setLoginUser(user, loginType, request, recordLog);
	}

	public static IUser getLoginUser() {
		IUser local = ThreadLocalManager.getUserLocal();
		if (local != null) {
			return local;
		}
		return sessionUser().orElseGet(User::anonymous);
	}

	public static Long getLoginUserId() {
		Long id = getLoginUser().getLoginUserId();
		return id != null ? id : Constants.ANONYMOUS_USER_ID;
	}

	public static boolean isAnonymous() {
		return Constants.ANONYMOUS_USER_ID == getLoginUserId();
	}

	public static boolean isOpAllow(Long menuId, String opCode) {
		return instance.operationPermissionEvaluator.isOpAllowForCurrentUser(menuId, opCode);
	}

	public static List<String> getOpCodes(Long menuId) {
		return instance.operationPermissionEvaluator.getOpCodesForCurrentUser(menuId);
	}

	private static Optional<IUser> sessionUser() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			return Optional.empty();
		}
		HttpSession session = attrs.getRequest().getSession(false);
		if (session == null) {
			return Optional.empty();
		}
		Object raw = session.getAttribute(Constants.SESSION_USER);
		if (raw instanceof IUser iu) {
			return Optional.of(iu);
		}
		return Optional.empty();
	}
}
