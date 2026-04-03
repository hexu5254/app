package com.app.platform.admin;

import com.app.platform.core.authentication.Constants;
import com.app.platform.org.employee.domain.SysEmployee;
import com.app.platform.org.employee.repository.SysEmployeeRepository;
import com.app.platform.sm.menu.domain.AppMenu;
import com.app.platform.sm.menu.domain.AppOpAssign;
import com.app.platform.sm.menu.domain.AppOpSecurity;
import com.app.platform.sm.menu.repository.AppMenuRepository;
import com.app.platform.sm.menu.repository.AppOpAssignRepository;
import com.app.platform.sm.menu.repository.AppOpSecurityRepository;
import com.app.platform.sm.role.domain.AppRole;
import com.app.platform.sm.role.domain.SmRoleUser;
import com.app.platform.sm.role.repository.AppRoleRepository;
import com.app.platform.sm.role.repository.SmRoleUserRepository;
import com.app.platform.sm.user.domain.SmUser;
import com.app.platform.sm.user.repository.SmUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAndPermissionIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SmUserRepository smUserRepository;

	@Autowired
	private SysEmployeeRepository sysEmployeeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AppMenuRepository appMenuRepository;

	@Autowired
	private AppOpSecurityRepository appOpSecurityRepository;

	@Autowired
	private AppOpAssignRepository appOpAssignRepository;

	@Autowired
	private AppRoleRepository appRoleRepository;

	@Autowired
	private SmRoleUserRepository smRoleUserRepository;

	@BeforeEach
	void reset() {
		appOpAssignRepository.deleteAll();
		appOpSecurityRepository.deleteAll();
		appMenuRepository.deleteAll();
		smRoleUserRepository.deleteAll();
		sysEmployeeRepository.deleteAll();
		smUserRepository.deleteAll();
		appRoleRepository.deleteAll();

		SmUser sm = new SmUser();
		sm.setCode("zhangsan");
		sm.setName("张三");
		sm.setPasswordBcrypt(passwordEncoder.encode("secret"));
		sm.setUserType((short) 0);
		sm.setStatus(Constants.USER_STATUS_NORMAL);
		sm.setFailedLoginCount(0);
		smUserRepository.save(sm);

		SysEmployee emp = new SysEmployee();
		emp.setSmUser(sm);
		emp.setStatus(Constants.USER_STATUS_NORMAL);
		emp.setCode("zhangsan");
		emp.setName("张三");
		emp.setUserId(sm.getId());
		sysEmployeeRepository.save(emp);
	}

	private MockHttpSession loginZhangsan() throws Exception {
		var r = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
						.session(new MockHttpSession())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"secret\"}"))
				.andExpect(status().isOk())
				.andReturn();
		return (MockHttpSession) r.getRequest().getSession(false);
	}

	@Test
	void normalUser_cannotAccessAdminUsers() throws Exception {
		MockHttpSession session = loginZhangsan();
		mockMvc.perform(get("/api/admin/users").session(session))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	@Test
	void opCodes_requiresLogin() throws Exception {
		mockMvc.perform(get("/api/permissions/menus/1/op-codes"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void opCodes_menuNotFound() throws Exception {
		MockHttpSession session = loginZhangsan();
		mockMvc.perform(get("/api/permissions/menus/99999/op-codes").session(session))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("MENU_NOT_FOUND"));
	}

	@Test
	void opCodes_assignedRole_returnsCode() throws Exception {
		AppMenu menu = new AppMenu();
		menu.setName("测试菜单");
		menu.setMenuType(Constants.MENU_TYPE_EMP);
		menu.setClientType("1");
		menu.setStatus("1");
		appMenuRepository.save(menu);

		AppOpSecurity op = new AppOpSecurity();
		op.setMenu(menu);
		op.setCode("ADD");
		op.setStatus("1");
		appOpSecurityRepository.save(op);

		AppRole role = new AppRole();
		role.setCode("r1");
		role.setName("角色1");
		role.setStatus("1");
		appRoleRepository.save(role);

		AppOpAssign assign = new AppOpAssign();
		assign.setRole(role);
		assign.setOp(op);
		appOpAssignRepository.save(assign);

		SmUser sm = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		SmRoleUser ru = new SmRoleUser();
		ru.setUserId(sm.getId());
		ru.setRoleId(role.getId());
		smRoleUserRepository.save(ru);

		MockHttpSession session = loginZhangsan();
		mockMvc.perform(get("/api/permissions/menus/" + menu.getId() + "/op-codes").session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.opCodes[0]").value("ADD"));
	}

	@Test
	void admin_canReplaceRoleMenuPermissions_and_visibleMenus() throws Exception {
		AppMenu menu = new AppMenu();
		menu.setName("根菜单");
		menu.setMenuType(Constants.MENU_TYPE_EMP);
		menu.setClientType("1");
		menu.setStatus("1");
		menu.setSequ(0);
		appMenuRepository.save(menu);

		AppOpSecurity op = new AppOpSecurity();
		op.setMenu(menu);
		op.setCode("VIEW");
		op.setStatus("1");
		op.setSequ(0);
		appOpSecurityRepository.save(op);

		AppRole role = new AppRole();
		role.setCode("r_admin_test");
		role.setName("测试角色");
		role.setStatus("1");
		role.setSequ(0);
		role.setIsInner("0");
		role.setIsViewAll("0");
		appRoleRepository.save(role);

		SmUser adminUser = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		adminUser.setUserType((short) 9);
		smUserRepository.save(adminUser);

		MockHttpSession adminSession = loginZhangsan();
		mockMvc.perform(put("/api/admin/roles/" + role.getId() + "/menu-permissions")
						.param("clientType", "1")
						.session(adminSession)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"opIds\":[" + op.getId() + "]}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.opIds[0]").value(op.getId().intValue()));

		SmUser sm = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		sm.setUserType((short) 0);
		smUserRepository.save(sm);
		SmRoleUser ru = new SmRoleUser();
		ru.setUserId(sm.getId());
		ru.setRoleId(role.getId());
		smRoleUserRepository.save(ru);

		MockHttpSession userSession = loginZhangsan();
		mockMvc.perform(get("/api/permissions/menus/visible").param("clientType", "1").session(userSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(menu.getId().intValue()));
	}

	@Test
	void admin_canListRoleSelectOptions() throws Exception {
		AppRole role = new AppRole();
		role.setCode("opt_r");
		role.setName("可选角色");
		role.setStatus("1");
		role.setSequ(0);
		role.setIsInner("0");
		role.setIsViewAll("0");
		appRoleRepository.save(role);

		SmUser adminUser = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		adminUser.setUserType((short) 9);
		smUserRepository.save(adminUser);

		MockHttpSession adminSession = loginZhangsan();
		mockMvc.perform(get("/api/admin/roles/select-options").session(adminSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(role.getId().intValue()))
				.andExpect(jsonPath("$.data[0].code").value("opt_r"))
				.andExpect(jsonPath("$.data[0].name").value("可选角色"));
	}

	@Test
	void admin_canListMenuOpSelectOptions() throws Exception {
		AppMenu menu = new AppMenu();
		menu.setName("菜单A");
		menu.setMenuType(Constants.MENU_TYPE_EMP);
		menu.setClientType("1");
		menu.setStatus("1");
		menu.setSequ(0);
		appMenuRepository.save(menu);

		AppOpSecurity op = new AppOpSecurity();
		op.setMenu(menu);
		op.setCode("VIEW_A");
		op.setName("查看");
		op.setStatus("1");
		op.setSequ(0);
		appOpSecurityRepository.save(op);

		SmUser adminUser = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		adminUser.setUserType((short) 9);
		smUserRepository.save(adminUser);

		MockHttpSession adminSession = loginZhangsan();
		mockMvc.perform(get("/api/admin/menu-ops/select-options").param("clientType", "1").session(adminSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(op.getId().intValue()))
				.andExpect(jsonPath("$.data[0].code").value("VIEW_A"))
				.andExpect(jsonPath("$.data[0].menuName").value("菜单A"));
	}

	@Test
	void admin_canListMenuTreeRows() throws Exception {
		AppMenu parent = new AppMenu();
		parent.setName("父菜单");
		parent.setMenuType(Constants.MENU_TYPE_EMP);
		parent.setClientType("1");
		parent.setStatus("1");
		parent.setSequ(0);
		appMenuRepository.save(parent);

		AppMenu child = new AppMenu();
		child.setName("子菜单");
		child.setMenuType(Constants.MENU_TYPE_EMP);
		child.setClientType("1");
		child.setStatus("1");
		child.setSequ(0);
		child.setParent(parent);
		appMenuRepository.save(child);

		SmUser adminUser = smUserRepository.findByCodeIgnoreCase("zhangsan").orElseThrow();
		adminUser.setUserType((short) 9);
		smUserRepository.save(adminUser);

		MockHttpSession adminSession = loginZhangsan();
		mockMvc.perform(get("/api/admin/menus/tree-rows").param("clientType", "1").session(adminSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].name").value("父菜单"))
				.andExpect(jsonPath("$.data[1].name").value("子菜单"))
				.andExpect(jsonPath("$.data[1].parentId").value(parent.getId().intValue()));
	}
}
