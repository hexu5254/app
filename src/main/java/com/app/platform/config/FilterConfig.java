package com.app.platform.config;

import com.app.platform.auth.filter.AdminAuthorizationFilter;
import com.app.platform.auth.filter.ApiAccessControlFilter;
import com.app.platform.auth.filter.AuthContextFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<AuthContextFilter> authContextFilterRegistration() {
		FilterRegistrationBean<AuthContextFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new AuthContextFilter());
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public FilterRegistrationBean<ApiAccessControlFilter> apiAccessControlFilterRegistration(
			AuthProperties authProperties,
			ObjectMapper objectMapper) {
		FilterRegistrationBean<ApiAccessControlFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new ApiAccessControlFilter(authProperties, objectMapper));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 30);
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public FilterRegistrationBean<AdminAuthorizationFilter> adminAuthorizationFilterRegistration(
			AuthProperties authProperties,
			ObjectMapper objectMapper) {
		FilterRegistrationBean<AdminAuthorizationFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new AdminAuthorizationFilter(authProperties, objectMapper));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 35);
		bean.addUrlPatterns("/*");
		return bean;
	}
}
