package com.app.platform.api.admin;

import com.app.platform.admin.AdminMenuService;
import com.app.platform.admin.AdminRoleService;
import com.app.platform.api.dto.ApiSuccessBody;
import com.app.platform.api.dto.admin.AdminCreateRoleRequest;
import com.app.platform.api.dto.admin.AdminPatchRoleRequest;
import com.app.platform.api.dto.admin.AdminReplaceRoleOpsRequest;
import com.app.platform.api.dto.admin.AdminRoleDetailDto;
import com.app.platform.api.dto.admin.AdminRoleOptionDto;
import com.app.platform.api.dto.admin.AdminMenuAssignNodeDto;
import com.app.platform.api.dto.admin.AdminRoleRowDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

	private final AdminRoleService adminRoleService;
	private final AdminMenuService adminMenuService;

	public AdminRoleController(AdminRoleService adminRoleService, AdminMenuService adminMenuService) {
		this.adminRoleService = adminRoleService;
		this.adminMenuService = adminMenuService;
	}

	@GetMapping("/menu-assign-tree")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuAssignNodeDto>>> menuAssignTree(
			@RequestParam(defaultValue = "1") String clientType) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuService.buildAssignTree(clientType)));
	}

	@GetMapping
	public ResponseEntity<ApiSuccessBody<Map<String, Object>>> list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String sort) {
		Page<AdminRoleRowDto> pg = adminRoleService.list(page, size, sort);
		Map<String, Object> body = Map.of(
				"content", pg.getContent(),
				"totalElements", pg.getTotalElements(),
				"totalPages", pg.getTotalPages(),
				"number", pg.getNumber(),
				"size", pg.getSize());
		return ResponseEntity.ok(ApiSuccessBody.of(body));
	}

	@GetMapping("/select-options")
	public ResponseEntity<ApiSuccessBody<List<AdminRoleOptionDto>>> selectOptions() {
		return ResponseEntity.ok(ApiSuccessBody.of(adminRoleService.listSelectOptions()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<AdminRoleDetailDto>> detail(
			@PathVariable long id,
			@RequestParam(defaultValue = "1") String clientType) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminRoleService.getDetail(id, clientType)));
	}

	@PostMapping
	public ResponseEntity<ApiSuccessBody<AdminRoleRowDto>> create(@Valid @RequestBody AdminCreateRoleRequest req) {
		return ResponseEntity.status(201).body(ApiSuccessBody.of(adminRoleService.create(req)));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<AdminRoleRowDto>> patch(
			@PathVariable long id,
			@Valid @RequestBody AdminPatchRoleRequest req) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminRoleService.patch(id, req)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessBody<Map<String, Boolean>>> delete(@PathVariable long id) {
		adminRoleService.deleteLogical(id);
		return ResponseEntity.ok(ApiSuccessBody.of(Map.of("success", true)));
	}

	@PutMapping("/{id}/menu-permissions")
	public ResponseEntity<ApiSuccessBody<Map<String, List<Long>>>> replaceMenuPermissions(
			@PathVariable long id,
			@RequestParam(defaultValue = "1") String clientType,
			@RequestBody AdminReplaceRoleOpsRequest req) {
		List<Long> opIds = adminRoleService.replaceMenuPermissions(id, clientType, req);
		return ResponseEntity.ok(ApiSuccessBody.of(Map.of("opIds", opIds)));
	}
}
