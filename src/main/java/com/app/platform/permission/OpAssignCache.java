package com.app.platform.permission;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 单租户操作码缓存（无 tenantId 前缀）。角色变更时按用户失效。
 */
@Component
public class OpAssignCache {

	private final ConcurrentHashMap<String, List<String>> menuAllOps = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, List<String>> userMenuAssignedOps = new ConcurrentHashMap<>();

	public List<String> getMenuOpCodes(long menuId, Supplier<List<String>> loader) {
		String key = "m:" + menuId;
		return menuAllOps.computeIfAbsent(key, k -> List.copyOf(loader.get()));
	}

	public List<String> getAssignedOpCodes(long userId, long menuId, Supplier<List<String>> loader) {
		String key = userId + ":" + menuId;
		return userMenuAssignedOps.computeIfAbsent(key, k -> List.copyOf(loader.get()));
	}

	public void evictUser(long userId) {
		String prefix = userId + ":";
		userMenuAssignedOps.keySet().removeIf(key -> key.startsWith(prefix));
	}

	public void evictMenu(long menuId) {
		menuAllOps.remove("m:" + menuId);
		userMenuAssignedOps.keySet().removeIf(key -> key.endsWith(":" + menuId));
	}

	public void evictAll() {
		menuAllOps.clear();
		userMenuAssignedOps.clear();
	}
}
