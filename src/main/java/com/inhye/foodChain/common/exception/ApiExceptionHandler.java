package com.inhye.foodChain.common.exception;

import com.inhye.foodChain.common.dto.ApiErrorResponse;
import com.inhye.foodChain.product.controller.ProductApiController;
import com.inhye.foodChain.stock.controller.StockApiController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(assignableTypes = {StockApiController.class, ProductApiController.class})
public class ApiExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(
			ResourceNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiErrorResponse> handleNoSuchElement(
			NoSuchElementException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.", request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiErrorResponse> handleConflict(
			ConflictException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
			DataIntegrityViolationException ex, HttpServletRequest request) {
		log.warn("Data integrity violation: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, "이미 존재하는 데이터이거나 제약 조건을 위반했습니다.", request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> details = new LinkedHashMap<>();
		ex.getBindingResult()
				.getFieldErrors()
				.forEach(error -> details.put(error.getField(), error.getDefaultMessage()));
		ex.getBindingResult()
				.getGlobalErrors()
				.forEach(error -> details.put(error.getObjectName(), error.getDefaultMessage()));
		String message = details.isEmpty() ? "요청 값이 올바르지 않습니다." : details.values().iterator().next();
		return build(HttpStatus.BAD_REQUEST, message, request, details);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex, HttpServletRequest request) {
		Map<String, String> details = new LinkedHashMap<>();
		ex.getConstraintViolations()
				.forEach(v -> details.put(v.getPropertyPath().toString(), v.getMessage()));
		return build(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다.", request, details);
	}

	@ExceptionHandler({
		MissingServletRequestParameterException.class,
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
			Exception ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.", request);
	}

	@ExceptionHandler(DataAccessResourceFailureException.class)
	public ResponseEntity<ApiErrorResponse> handleDbUnavailable(
			DataAccessResourceFailureException ex, HttpServletRequest request) {
		log.error("Database unavailable", ex);
		return build(HttpStatus.SERVICE_UNAVAILABLE, "데이터베이스에 연결할 수 없습니다.", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Unexpected API error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", request);
	}

	private ResponseEntity<ApiErrorResponse> build(
			HttpStatus status, String message, HttpServletRequest request) {
		return build(status, message, request, null);
	}

	private ResponseEntity<ApiErrorResponse> build(
			HttpStatus status, String message, HttpServletRequest request, Map<String, String> details) {
		ApiErrorResponse body =
				details == null
						? ApiErrorResponse.of(
								status.value(), status.getReasonPhrase(), message, request.getRequestURI())
						: ApiErrorResponse.of(
								status.value(),
								status.getReasonPhrase(),
								message,
								request.getRequestURI(),
								details);
		return ResponseEntity.status(status).body(body);
	}
}
