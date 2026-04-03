package com.app.platform.api.admin;

import com.app.platform.admin.AdminMenuService;
import com.app.platform.api.dto.ApiSuccessBody;
import com.app.platform.api.dto.admin.AdminCreateMenuRequest;
import com.app.platform.api.dto.admin.AdminMenuAssignNodeDto;
import com.app.platform.api.dto.admin.AdminMenuOpDefDto;
import com.app.platform.api.dto.admin.AdminMenuRowDto;
import com.app.platform.api.dto.admin.AdminMenuTreeRowDto;
import com.app.platform.api.dto.admin.AdminPatchMenuRequest;
import com.app.platform.api.dto.admin.AdminReplaceMenuOpDefsRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/menus")
public class AdminMenuController {

	private final AdminMenuService adminMenuService;

	public AdminMenuController(AdminMenuService adminMenuService) {
		this.adminMenuService = adminMenuService;
	}

	@GetMapping("/tree-rows")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuTreeRowDto>>> treeRows(
			@RequestParam(defaultValue = "1") String clientType) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.listTreeRows(clientType)));
	}

	@GetMapping("/assign-tree")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuAssignNodeDto>>> assignTree(
			@RequestParam(defaultValue = "1") String clientType) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.buildAssignTree(clientType)));
	}

	@PostMapping
	public ResponseEntity<ApiSuccessBody<AdminMenuRowDto>> create(@Valid @RequestBody AdminCreateMenuRequest req) {
		return ResponseEntity.status(201).body(ApiSuccessBody.of(adminMenuService.create(req)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<AdminMenuRowDto>> getOne(@PathVariable long id) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.getRow(id)));
	}

	@GetMapping("/{id}/op-definitions")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuOpDefDto>>> opDefinitions(@PathVariable long id) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.listOpDefinitions(id)));
	}

	@PutMapping("/{id}/op-definitions")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuOpDefDto>>> replaceOpDefinitions(
			@PathVariable long id,
			@Valid @RequestBody AdminReplaceMenuOpDefsRequest req) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.replaceOpDefinitions(id, req)));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<AdminMenuRowDto>> patch(
			@PathVariable long id,
			@Valid @RequestBody AdminPatchMenuRequest req) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.patch(id, req)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<Map<String, Boolean>>> delete(@PathVariable long id) {
		adminMenuService.deleteLogical(id);
		return ResponseEntity.ok(ApiSuccessBody.of(Map.of("success", true)));
	}
}
