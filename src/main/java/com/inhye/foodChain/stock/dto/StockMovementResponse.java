package com.inhye.foodChain.stock.dto;

import com.inhye.foodChain.stock.domain.MovementType;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockMovement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "재고 이력 응답")
public record StockMovementResponse(
		@Schema(description = "이력 ID", example = "1") Long movementId,
		@Schema(description = "재고 ID", example = "1") Long stockId,
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "상품명", example = "삼각김밥") String productName,
		@Schema(description = "LOT 번호", example = "LOT-20250101-001") String lotNo,
		@Schema(description = "이력 유형") MovementType movementType,
		@Schema(description = "이력 유형 표시명", example = "입고") String movementTypeLabel,
		@Schema(description = "수량", example = "100") int quantity,
		@Schema(description = "사유", example = "재고 입고") String reason,
		@Schema(description = "발생 일시") LocalDateTime createdAt) {

	public static StockMovementResponse from(StockMovement movement) {
		Stock stock = movement.getStock();
		MovementType movementType = movement.getMovementType();
		return new StockMovementResponse(
				movement.getMovementId(),
				stock.getStockId(),
				movement.getProductId(),
				stock.getProductName(),
				stock.getLotNo(),
				movementType,
				movementType.getDisplayName(),
				movement.getQuantity(),
				movement.getReason(),
				movement.getCreatedAt());
	}
}
