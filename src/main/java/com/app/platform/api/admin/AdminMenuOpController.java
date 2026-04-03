package com.app.platform.api.admin;

import com.app.platform.admin.AdminMenuOpService;
import com.app.platform.api.dto.ApiSuccessBody;
import com.app.platform.api.dto.admin.AdminMenuOpOptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu-ops")
public class AdminMenuOpController {

	private final AdminMenuOpService adminMenuOpService;

	public AdminMenuOpController(AdminMenuOpService adminMenuOpService) {
		this.adminMenuOpService = adminMenuOpService;
	}

	@GetMapping("/select-options")
	public ResponseEntity<ApiSuccessBody<List<AdminMenuOpOptionDto>>> selectOptions(
			@RequestParam(defaultValue = "1") String clientType) {
		return ResponseEntity.ok(ApiSuccessBody.of(adminMenuOpService.listSelectOptions(clientType)));
	}
}
