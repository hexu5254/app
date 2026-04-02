package com.app.platform.api.auth;

import com.app.platform.api.dto.ApiSuccessBody;
import com.app.platform.api.dto.LoginRequest;
import com.app.platform.api.dto.LogoutResponse;
import com.app.platform.api.dto.RegisterRequest;
import com.app.platform.api.dto.UserSessionDto;
import com.app.platform.auth.AuthContextHolder;
import com.app.platform.auth.AuthenticatedContext;
import com.app.platform.exception.UnauthorizedException;
import com.app.platform.service.LoginService;
import com.app.platform.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final LoginService loginService;
	private final RegistrationService registrationService;

	public AuthController(LoginService loginService, RegistrationService registrationService) {
		this.loginService = loginService;
		this.registrationService = registrationService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiSuccessBody<UserSessionDto>> register(@Valid @RequestBody RegisterRequest request) {
		UserSessionDto data = registrationService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessBody.of(data));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiSuccessBody<UserSessionDto>> login(@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {
		UserSessionDto data = loginService.login(request, httpRequest);
		return ResponseEntity.ok(ApiSuccessBody.of(data));
	}

	@PostMapping("/logout")
	public ResponseEntity<LogoutResponse> logout(HttpServletRequest httpRequest) {
		loginService.logout(httpRequest);
		return ResponseEntity.ok(LogoutResponse.ok());
	}

	@GetMapping("/me")
	public ResponseEntity<ApiSuccessBody<UserSessionDto>> me() {
		AuthenticatedContext ctx = AuthContextHolder.get();
		if (ctx == null) {
			throw new UnauthorizedException();
		}
		UserSessionDto dto = new UserSessionDto(ctx.getUserId(), ctx.getLoginName(), ctx.getDisplayName());
		return ResponseEntity.ok(ApiSuccessBody.of(dto));
	}
}
