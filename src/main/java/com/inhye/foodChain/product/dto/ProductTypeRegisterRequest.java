package com.inhye.foodChain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 유형 등록 요청")
public record ProductTypeRegisterRequest(
		@NotBlank
				@Size(max = 20)
				@Schema(description = "유형 코드 (상품 ID 접두어)", example = "BAB")
				String typeCode,
		@NotBlank @Size(max = 50) @Schema(description = "유형명", example = "밥류") String typeName) {}
