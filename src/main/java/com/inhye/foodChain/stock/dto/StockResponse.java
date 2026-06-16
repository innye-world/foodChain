package com.inhye.foodChain.stock.dto;

import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.domain.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "재고 응답")
public record StockResponse(
		@Schema(description = "재고 ID", example = "1") Long stockId,
		@Schema(description = "상품 ID", example = "BAB-001") String productId,
		@Schema(description = "상품명", example = "삼각김밥") String productName,
		@Schema(description = "LOT 번호", example = "LOT-20250101-001") String lotNo,
		@Schema(description = "제조일자") LocalDate mfgDate,
		@Schema(description = "유통기한") LocalDate expiryDate,
		@Schema(description = "입고일시") LocalDateTime receivedAt,
		@Schema(description = "수량", example = "100") int amount,
		@Schema(description = "재고 상태") StockStatus stockStatus) {

	public static StockResponse from(Stock stock) {
		return new StockResponse(
				stock.getStockId(),
				stock.getProductId(),
				stock.getProductName(),
				stock.getLotNo(),
				stock.getMfgDate(),
				stock.getExpiryDate(),
				stock.getReceivedAt(),
				stock.getAmount(),
				stock.getStockStatus());
	}
}
