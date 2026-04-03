package com.app.platform.exception;

public class InvalidRoleException extends RuntimeException {

	public InvalidRoleException() {
		super("角色无效或不可用");
	}
}
