package com.inhye.foodChain.outbound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "출고 요청 (MVP: 상품 1건)")
public record OutboundOrderRequest(
		@NotBlank @Schema(description = "외부 주문 ID", example = "ORD-20260630-001") String orderId,
		@Schema(description = "요청자", example = "홍길동") String requestedBy,
		@NotBlank @Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Positive @Schema(description = "요청 수량", example = "350") int requestAmount) {}
