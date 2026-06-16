package com.inhye.foodChain.product.dto;

import com.inhye.foodChain.product.domain.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 유형 응답")
public record ProductTypeResponse(
		@Schema(description = "상품 유형 ID", example = "1") Long productTypeId,
		@Schema(description = "유형 코드", example = "BAB") String typeCode,
		@Schema(description = "유형명", example = "밥류") String typeName) {

	public static ProductTypeResponse from(ProductType productType) {
		return new ProductTypeResponse(
				productType.getProductTypeId(),
				productType.getTypeCode(),
				productType.getTypeName());
	}
}
