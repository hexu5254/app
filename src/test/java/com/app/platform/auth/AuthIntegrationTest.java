package com.app.platform.auth;

import com.app.platform.domain.AppUser;
import com.app.platform.domain.UserStatus;
import com.app.platform.repository.AppUserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void resetData() {
		appUserRepository.deleteAll();

		AppUser user = new AppUser();
		user.setLoginName("zhangsan");
		user.setDisplayName("张三");
		user.setPasswordHash(passwordEncoder.encode("secret"));
		user.setStatus(UserStatus.NORMAL.getCode());
		user.setFailedLoginCount(0);
		appUserRepository.save(user);
	}

	@Test
	void ac1_correctLogin_thenMe() throws Exception {
		MockHttpSession sessionBefore = new MockHttpSession();
		var loginResult = mockMvc.perform(post("/api/auth/login")
						.session(sessionBefore)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"secret\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.userId").exists())
				.andExpect(jsonPath("$.data.loginName").value("zhangsan"))
				.andReturn();

		MockHttpSession sessionAfterLogin = (MockHttpSession) loginResult.getRequest().getSession(false);
		assertThat(sessionAfterLogin).isNotNull();

		mockMvc.perform(get("/api/auth/me").session(sessionAfterLogin))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.loginName").value("zhangsan"));
	}

	@Test
	void ac2_wrongPassword_sameMessageAsMissingUser() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"bad\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("AUTH_FAILED"))
				.andExpect(jsonPath("$.message").value("账号或密码错误"));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"nobody\",\"password\":\"secret\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("账号或密码错误"));
	}

	@Test
	void ac4_disabledUser() throws Exception {
		AppUser u = appUserRepository.findByLoginName("zhangsan").orElseThrow();
		u.setStatus(UserStatus.DISABLED.getCode());
		appUserRepository.save(u);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"secret\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("账号或密码错误"));
	}

	@Test
	void ac6_logoutThenMe401() throws Exception {
		var loginResult = mockMvc.perform(post("/api/auth/login")
						.session(new MockHttpSession())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"secret\"}"))
				.andExpect(status().isOk())
				.andReturn();
		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
		assertThat(session).isNotNull();

		mockMvc.perform(post("/api/auth/logout").session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true));

		mockMvc.perform(get("/api/auth/me").session(session))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void ac7_meWithoutSession401() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
	}

	@Test
	void actuatorHealthAnonymous() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk());
	}

	@Test
	void validationError400() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"\",\"password\":\"x\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void registerCreatesUser_201() throws Exception {
		appUserRepository.deleteAll();

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"newuser\",\"password\":\"pass12345\",\"displayName\":\"新用户\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.loginName").value("newuser"))
				.andExpect(jsonPath("$.data.displayName").value("新用户"));

		assertThat(appUserRepository.findByLoginName("newuser")).isPresent();
	}

	@Test
	void registerDuplicate_409() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"zhangsan\",\"password\":\"other\"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("USERNAME_TAKEN"))
				.andExpect(jsonPath("$.message").value("用户名已被使用"));
	}

	@Test
	void registerDoesNotCreateSession() throws Exception {
		appUserRepository.deleteAll();

		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/api/auth/register")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"loginName\":\"solo\",\"password\":\"secret99\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/auth/me").session(session))
				.andExpect(status().isUnauthorized());
	}
}
