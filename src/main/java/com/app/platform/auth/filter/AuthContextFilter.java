package com.app.platform.auth.filter;

import com.app.platform.auth.AuthContextHolder;
import com.app.platform.auth.AuthenticatedContext;
import com.app.platform.config.AuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthContextFilter extends OncePerRequestFilter {

	private final AuthProperties authProperties;

	public AuthContextFilter(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object raw = session.getAttribute(authProperties.getSessionAttributeName());
				if (raw instanceof AuthenticatedContext ac) {
					AuthContextHolder.set(ac);
				}
			}
			filterChain.doFilter(request, response);
		}
		finally {
			AuthContextHolder.clear();
		}
	}
}
