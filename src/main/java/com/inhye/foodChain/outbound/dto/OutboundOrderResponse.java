package com.inhye.foodChain.outbound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "출고 응답 (MVP: 상품 1건)")
public record OutboundOrderResponse(
		@Schema(description = "외부 주문 ID", example = "ORD-20260630-001") String orderId,
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "요청 수량", example = "350") int requestAmount,
		@Schema(description = "실제 출고 수량", example = "350") int fulfilledAmount,
		@Schema(description = "LOT별 출고 배정 목록") List<LotAllocationResponse> lotAllocations) {}
