package com.app.platform.api.dto.permission;

import java.util.ArrayList;
import java.util.List;

public class MenuVisibleNodeDto {

	private Long id;
	private Long parentId;
	private String name;
	private String path;
	private String icon;
	private String menuType;
	private int sequ;
	private final List<MenuVisibleNodeDto> children = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public int getSequ() {
		return sequ;
	}

	public void setSequ(int sequ) {
		this.sequ = sequ;
	}

	public List<MenuVisibleNodeDto> getChildren() {
		return children;
	}
}
