package com.app.platform.core.authentication;

/**
 * 与会话、登录类型、状态码相关的常量（单环境，不含多租户）。
 */
public final class Constants {

	private Constants() {
	}

	/** HttpSession 中存放 {@link IUser} 的属性名（与历史基座 {@code Constants.SESSION_USER} 语义一致）。 */
	public static final String SESSION_USER = "user";

	public static final String LOGIN_TYPE_WEB = "WEB";
	public static final String LOGIN_TYPE_CLIENT = "CLIENT";

	/** 未登录占位用户 ID（避免 NPE，具体值以项目约定为准）。 */
	public static final long ANONYMOUS_USER_ID = -10_000L;

	/**
	 * 历史占位；实际「企业管理员 id → IS_ADMIN_EMP」以 {@code app.auth.enterprise-admin-user-id} 为准（默认 1）。
	 */
	public static final long ADMIN_ID = 1L;

	/** sm_user / sys_employee.status：删除 */
	public static final String USER_STATUS_DELETED = "0";
	/** 正常 */
	public static final String USER_STATUS_NORMAL = "1";
	/** 冻结 */
	public static final String USER_STATUS_FREEZE = "2";

	/** sm_user.user_type：系统管理员 */
	public static final short USER_TYPE_SYS_ADMIN = 9;

	/** 与参考 IUser.ORIGIN_TYPE 员工端一致 */
	public static final String ORIGIN_TYPE_EMPLOYEE = "1";

	/** app_menu.menu_type */
	public static final String MENU_TYPE_ALL = "0";
	public static final String MENU_TYPE_ADMIN = "1";
	public static final String MENU_TYPE_EMP = "2";
	public static final String MENU_TYPE_SUPER_ADMIN = "3";

	/** 批量查询菜单操作码上限 */
	public static final int MAX_MENU_IDS_BATCH = 50;

	/** app_role.code：注册默认绑定（须与 Flyway 种子一致） */
	public static final String APP_ROLE_CODE_NORMAL_USER = "normal_user";

	/** app_role.code：应用级「管理员」全量菜单操作码（与 {@link #USER_TYPE_SYS_ADMIN} 平台管理员不同） */
	public static final String APP_ROLE_CODE_ADMIN = "admin";
}
