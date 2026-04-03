package com.app.platform.exception;

public class MenuNotFoundException extends RuntimeException {

	public MenuNotFoundException() {
		super("菜单不存在");
	}
}
