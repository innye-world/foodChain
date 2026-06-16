package com.inhye.foodChain.product.dto;

import com.inhye.foodChain.stock.domain.StorageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "상품 등록 요청")
public record ProductRegisterRequest(
		@Schema(description = "상품 유형 ID", example = "1") Long productTypeId,
		@Schema(description = "상품명", example = "삼각김밥") String productName,
		@Schema(description = "보관 유형") StorageType storageType,
		@Schema(description = "최저 보관 온도(℃)", example = "0.0") BigDecimal minTemperature,
		@Schema(description = "최고 보관 온도(℃)", example = "10.0") BigDecimal maxTemperature) {}
