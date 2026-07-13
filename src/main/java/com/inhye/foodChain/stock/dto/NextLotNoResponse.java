package com.inhye.foodChain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "다음 입고 LOT 번호")
public record NextLotNoResponse(
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "채번된 LOT 번호", example = "LOT-20260713-001") String lotNo) {}
