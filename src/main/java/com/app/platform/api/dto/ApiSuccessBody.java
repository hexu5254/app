package com.app.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiSuccessBody<T>(boolean success, T data) {

	public static <T> ApiSuccessBody<T> of(T data) {
		return new ApiSuccessBody<>(true, data);
	}

	public static ApiSuccessBody<Void> empty() {
		return new ApiSuccessBody<>(true, null);
	}
}
