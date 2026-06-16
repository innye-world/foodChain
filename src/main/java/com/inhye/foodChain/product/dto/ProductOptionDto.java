package com.inhye.foodChain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 선택 옵션 (드롭다운용)")
public record ProductOptionDto(
		@Schema(description = "상품 ID", example = "BAB-001") String productCode,
		@Schema(description = "상품명", example = "삼각김밥") String productName) {}
