package com.inhye.foodChain.product.dto;

import com.inhye.foodChain.stock.domain.StorageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "상품 등록 요청")
public record ProductRegisterRequest(
		@NotNull @Schema(description = "상품 유형 ID", example = "1") Long productTypeId,
		@NotBlank @Schema(description = "상품명", example = "삼각김밥") String productName,
		@NotNull @Schema(description = "보관 유형") StorageType storageType,
		@NotNull @Schema(description = "최저 보관 온도(℃)", example = "0.0") BigDecimal minTemperature,
		@NotNull @Schema(description = "최고 보관 온도(℃)", example = "10.0") BigDecimal maxTemperature,
		@Positive @Schema(description = "출고 경고 임박 일수 (유통기한 N일 이내)", example = "7")
				int warningThresholdDays,
		@NotNull
				@DecimalMin("0.1")
				@Schema(description = "출고 경고 잔여 유통기한 비율(%)", example = "20.0")
				BigDecimal warningThresholdPct) {}
