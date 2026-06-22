package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.stock.dto.StockMovementResponse;
import com.inhye.foodChain.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고 이력", description = "재고 입고·보류·상태 변경 등 stock_movement 이력 조회 API")
@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementApiController {

	private final StockService stockService;

	@Operation(
			summary = "재고 이력 목록 조회",
			description = "전체 재고 이력을 최신순(발생 일시 내림차순)으로 반환합니다.")
	@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = StockMovementResponse.class)))
	@GetMapping
	public List<StockMovementResponse> listMovements() {
		return stockService.findAllMovementsOrderByCreatedAtDesc().stream()
				.map(StockMovementResponse::from)
				.toList();
	}
}
