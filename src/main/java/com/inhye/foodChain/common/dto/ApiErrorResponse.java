package com.inhye.foodChain.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
		int status,
		String error,
		String message,
		String path,
		LocalDateTime timestamp,
		Map<String, String> details) {

	public static ApiErrorResponse of(int status, String error, String message, String path) {
		return new ApiErrorResponse(status, error, message, path, LocalDateTime.now(), null);
	}

	public static ApiErrorResponse of(
			int status, String error, String message, String path, Map<String, String> details) {
		return new ApiErrorResponse(status, error, message, path, LocalDateTime.now(), details);
	}
}
