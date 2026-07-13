package com.inhye.foodChain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "재고 입고 요청")
public record StockRegisterRequest(
		@NotBlank @Schema(description = "상품 ID", example = "BAB-001") String productId,
		@NotBlank @Schema(description = "LOT 번호", example = "LOT-20250101-001") String lotNo,
		@NotNull @Schema(description = "제조일자", example = "2026-01-01") LocalDate mfgDate,
		@NotNull @Schema(description = "유통기한", example = "2026-06-01") LocalDate expiryDate,
		@Positive @Schema(description = "입고 수량", example = "100") int amount,
		@NotNull @Schema(description = "입고 구역 현재 온도(℃)", example = "4.0") BigDecimal currentTemperature,
		@Schema(description = "토큰") String inboundToken) {}
