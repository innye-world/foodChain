package com.inhye.foodChain.outbound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "LOT별 출고 배정 내역")
public record LotAllocationResponse(
		@Schema(description = "LOT 번호", example = "LOT-0301") String lotNo,
		@Schema(description = "배정 수량", example = "300") int allocatedAmount,
		@Schema(description = "유통기한", example = "2026-07-01") LocalDate expiryDate) {}
