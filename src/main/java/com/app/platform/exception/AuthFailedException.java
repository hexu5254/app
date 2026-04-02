package com.app.platform.exception;

/**
 * Authentication failure; always mapped to HTTP 401 with a uniform public message.
 */
public class AuthFailedException extends RuntimeException {

	public AuthFailedException() {
		super("账号或密码错误");
	}
}
