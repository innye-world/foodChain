package com.inhye.foodChain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 유형 등록 요청")
public record ProductTypeRegisterRequest(
		@Schema(description = "유형 코드 (상품 ID 접두어)", example = "BAB") String typeCode,
		@Schema(description = "유형명", example = "밥류") String typeName) {}
