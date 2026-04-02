package com.app.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorBody(boolean success, String code, String message, Object details) {

	public static ApiErrorBody of(String code, String message) {
		return new ApiErrorBody(false, code, message, null);
	}

	public static ApiErrorBody withDetails(String code, String message, Object details) {
		return new ApiErrorBody(false, code, message, details);
	}
}
