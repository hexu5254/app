package com.app.platform.exception;

public class RoleNotFoundException extends RuntimeException {

	public RoleNotFoundException() {
		super("角色不存在");
	}
}
