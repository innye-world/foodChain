package com.inhye.foodChain.product.dto;

import com.inhye.foodChain.product.domain.Product;
import com.inhye.foodChain.stock.domain.StorageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "상품 응답")
public record ProductResponse(
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "상품명", example = "삼각김밥") String productName,
		@Schema(description = "유형 코드", example = "BAB") String typeCode,
		@Schema(description = "보관 유형") StorageType storageType,
		@Schema(description = "최저 보관 온도(℃)") BigDecimal minTemperature,
		@Schema(description = "최고 보관 온도(℃)") BigDecimal maxTemperature,
		@Schema(description = "출고 경고 임박 일수") int warningThresholdDays,
		@Schema(description = "출고 경고 잔여 유통기한 비율(%)") BigDecimal warningThresholdPct) {

	public static ProductResponse from(Product product) {
		return new ProductResponse(
				product.getProductId(),
				product.getProductName(),
				product.getProductType().getTypeCode(),
				product.getStorageType(),
				product.getMinTemperature(),
				product.getMaxTemperature(),
				product.getWarningThresholdDays(),
				product.getWarningThresholdPct());
	}
}
