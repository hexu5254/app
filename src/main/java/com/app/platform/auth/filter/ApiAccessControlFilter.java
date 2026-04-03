package com.app.platform.auth.filter;

import com.app.platform.api.dto.ApiErrorBody;
import com.app.platform.config.AuthProperties;
import com.app.platform.core.authentication.UserManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 在 {@link AuthContextFilter} 之后：除白名单外，{@code /api/**} 需已登录（非匿名 IUser）。
 */
public class ApiAccessControlFilter extends OncePerRequestFilter {

	private final AuthProperties authProperties;
	private final ObjectMapper objectMapper;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	private final List<PathRule> rules = new ArrayList<>();

	public ApiAccessControlFilter(AuthProperties authProperties, ObjectMapper objectMapper) {
		this.authProperties = authProperties;
		this.objectMapper = objectMapper;
		rebuildRules();
	}

	private void rebuildRules() {
		rules.clear();
		for (String entry : authProperties.getAnonymousPaths()) {
			int colon = entry.indexOf(':');
			if (colon <= 0 || colon == entry.length() - 1) {
				continue;
			}
			String method = entry.substring(0, colon).trim().toUpperCase();
			String pattern = entry.substring(colon + 1).trim();
			rules.add(new PathRule(method, pattern));
		}
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

		if (path.startsWith("/api/") && !isAnonymous(request.getMethod(), path)) {
			if (UserManager.isAnonymous()) {
				writeUnauthorized(response);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isAnonymous(String method, String path) {
		for (PathRule rule : rules) {
			if (rule.method().equalsIgnoreCase(method) && pathMatcher.match(rule.pattern(), path)) {
				return true;
			}
		}
		return false;
	}

	private void writeUnauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ApiErrorBody body = ApiErrorBody.of("UNAUTHORIZED", "未登录或会话已失效");
		objectMapper.writeValue(response.getOutputStream(), body);
	}

	private record PathRule(String method, String pattern) {
	}
}
