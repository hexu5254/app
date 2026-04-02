package com.app.platform.api.error;

import com.app.platform.api.dto.ApiErrorBody;
import com.app.platform.exception.AuthFailedException;
import com.app.platform.exception.UnauthorizedException;
import com.app.platform.exception.UsernameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthFailedException.class)
	public ResponseEntity<ApiErrorBody> authFailed(AuthFailedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiErrorBody.of("AUTH_FAILED", ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorBody> unauthorized(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiErrorBody.of("UNAUTHORIZED", ex.getMessage()));
	}

	@ExceptionHandler(UsernameTakenException.class)
	public ResponseEntity<ApiErrorBody> usernameTaken(UsernameTakenException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiErrorBody.of("USERNAME_TAKEN", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorBody> validation(MethodArgumentNotValidException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiErrorBody.of("VALIDATION_ERROR", "请求参数无效"));
	}
}
