package com.inhye.foodChain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "재고 입고 요청")
public record StockRegisterRequest(
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "LOT 번호", example = "LOT-20250101-001") String lotNo,
		@Schema(description = "제조일자", example = "2026-01-01") LocalDate mfgDate,
		@Schema(description = "유통기한", example = "2026-06-01") LocalDate expiryDate,
		@Schema(description = "입고 수량", example = "100") int amount,
		BigDecimal currentTemperature) {}
