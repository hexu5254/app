package com.app.platform.auth.filter;

import com.app.platform.api.dto.ApiErrorBody;
import com.app.platform.auth.AdminAuthSupport;
import com.app.platform.config.AuthProperties;
import com.app.platform.core.authentication.UserManager;
import com.app.platform.core.authentication.intf.IUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {@code /api/admin/**}：在已登录前提下要求平台管理员。
 */
public class AdminAuthorizationFilter extends OncePerRequestFilter {

	private final AuthProperties authProperties;
	private final ObjectMapper objectMapper;

	public AdminAuthorizationFilter(AuthProperties authProperties, ObjectMapper objectMapper) {
		this.authProperties = authProperties;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String path = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		if (!path.startsWith("/api/admin/")) {
			filterChain.doFilter(request, response);
			return;
		}

		IUser u = UserManager.getLoginUser();
		if (!AdminAuthSupport.isPlatformAdmin(u, authProperties.getAdminUserIds())) {
			writeForbidden(response, "需要管理员权限");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void writeForbidden(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getOutputStream(), ApiErrorBody.of("FORBIDDEN", message));
	}
}
