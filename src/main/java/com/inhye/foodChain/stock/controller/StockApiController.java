package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.stock.dto.StockRegisterRequest;
import com.inhye.foodChain.stock.dto.StockResponse;
import com.inhye.foodChain.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고", description = "재고 입고·조회 API. 자가품질검사 완료 상품만 입고하며, 목록은 FEFO(유통기한 → 입고일) 순으로 정렬됩니다.")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockApiController {

	private final StockService stockService;

	@Operation(
			summary = "재고 목록 조회",
			description = "전체 재고를 FEFO 순(유통기한 오름차순 → 입고일 오름차순)으로 반환합니다.")
	@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = StockResponse.class)))
	@GetMapping
	public List<StockResponse> listStocks() {
		return stockService.findAllStocksOrderByFefo().stream()
				.map(StockResponse::from)
				.toList();
	}

	@Operation(
			summary = "재고 입고",
			description = "새 LOT를 등록합니다. 입고 시 상태는 AVAILABLE이며, receivedAt은 서버 시각으로 기록됩니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "201",
				description = "입고 성공",
				content = @Content(schema = @Schema(implementation = StockResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 오류 또는 상품 미존재")
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StockResponse registerStock(@RequestBody StockRegisterRequest request) {
		return StockResponse.from(
				stockService.registerStock(
						request.productId(),
						request.lotNo(),
						request.mfgDate(),
						request.expiryDate(),
						request.amount()));
	}
}
