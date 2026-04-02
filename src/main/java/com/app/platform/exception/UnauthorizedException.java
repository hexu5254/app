package com.app.platform.exception;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super("未登录或会话已失效");
	}
}
