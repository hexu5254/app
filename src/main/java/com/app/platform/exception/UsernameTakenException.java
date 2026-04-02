package com.app.platform.exception;

public class UsernameTakenException extends RuntimeException {

	public UsernameTakenException() {
		super("用户名已被使用");
	}
}
