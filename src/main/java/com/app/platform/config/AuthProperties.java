package com.app.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

	/**
	 * HttpSession attribute key for {@link com.app.platform.auth.AuthenticatedContext}.
	 */
	private String sessionAttributeName = "AUTH_CONTEXT";

	private int bcryptStrength = 10;

	private int maxFailedLogins = 5;

	private int lockDurationMinutes = 15;

	/**
	 * Paths that skip authentication. Each entry: {@code METHOD:antPattern}, e.g. {@code POST:/api/auth/login}.
	 */
	private List<String> anonymousPaths = defaultAnonymousPaths();

	private static List<String> defaultAnonymousPaths() {
		List<String> list = new ArrayList<>();
		list.add("POST:/api/auth/register");
		list.add("POST:/api/auth/login");
		list.add("POST:/api/auth/logout");
		list.add("GET:/actuator/health");
		list.add("GET:/actuator/health/**");
		list.add("GET:/static/**");
		return list;
	}

	public String getSessionAttributeName() {
		return sessionAttributeName;
	}

	public void setSessionAttributeName(String sessionAttributeName) {
		this.sessionAttributeName = sessionAttributeName;
	}

	public int getBcryptStrength() {
		return bcryptStrength;
	}

	public void setBcryptStrength(int bcryptStrength) {
		this.bcryptStrength = bcryptStrength;
	}

	public int getMaxFailedLogins() {
		return maxFailedLogins;
	}

	public void setMaxFailedLogins(int maxFailedLogins) {
		this.maxFailedLogins = maxFailedLogins;
	}

	public int getLockDurationMinutes() {
		return lockDurationMinutes;
	}

	public void setLockDurationMinutes(int lockDurationMinutes) {
		this.lockDurationMinutes = lockDurationMinutes;
	}

	public List<String> getAnonymousPaths() {
		return anonymousPaths;
	}

	public void setAnonymousPaths(List<String> anonymousPaths) {
		this.anonymousPaths = anonymousPaths;
	}
}
