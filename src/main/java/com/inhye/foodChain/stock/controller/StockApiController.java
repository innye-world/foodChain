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
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고", description = "재고 입고·조회·보류 처리 API. 자가품질검사 완료 상품만 입고하며, 목록은 FEFO(유통기한 → 입고일) 순으로 정렬됩니다.")
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
			description = "새 LOT를 등록합니다. 입고 시에는 구역 온도를 입력하고 상품 등록 시에 저장했던 저장 온도와 비교합니다. 저장 온도에 적합하지 않은 상태라면 HOLD로 저장합니다. HOLD로 저장되는 경우에는 재고 이력 데이터에 히스토리를 추가합니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "201",
				description = "입고 성공",
				content = @Content(schema = @Schema(implementation = StockResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 오류"),
		@ApiResponse(responseCode = "404", description = "상품 미존재"),
		@ApiResponse(responseCode = "409", description = "중복 LOT 등 제약 조건 위반"),
		@ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StockResponse registerStock(@Valid @RequestBody StockRegisterRequest request) {
		return StockResponse.from(
				stockService.registerStock(
						request.productId(),
						request.lotNo(),
						request.mfgDate(),
						request.expiryDate(),
						request.amount(),
						request.currentTemperature()));
	}

	@Operation(
			summary = "보류 해제",
			description = "HOLD 상태 배치를 해제합니다. 유통기한이 지나지 않았으면 AVAILABLE, 경과했으면 EXPIRED로 전환합니다. STOCK_MOVEMENT에 RELEASE(quantity=0)를 기록합니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "해제 성공",
				content = @Content(schema = @Schema(implementation = StockResponse.class))),
		@ApiResponse(responseCode = "400", description = "HOLD 상태가 아님"),
		@ApiResponse(responseCode = "404", description = "재고 미존재"),
		@ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PatchMapping("/{stockId}/release")
	public StockResponse releaseStock(@PathVariable Long stockId) {
		return StockResponse.from(stockService.releaseStock(stockId));
	}

	@Operation(
			summary = "보류 배치 폐기",
			description = "HOLD 상태 배치를 폐기합니다. DISPOSED로 전환하고 수량을 0으로 만듭니다. STOCK_MOVEMENT에 DISPOSAL(quantity=기존 수량의 음수)을 기록합니다.")
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "폐기 성공",
				content = @Content(schema = @Schema(implementation = StockResponse.class))),
		@ApiResponse(responseCode = "400", description = "HOLD 상태가 아님"),
		@ApiResponse(responseCode = "404", description = "재고 미존재"),
		@ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PatchMapping("/{stockId}/dispose")
	public StockResponse disposeStock(@PathVariable Long stockId) {
		return StockResponse.from(stockService.disposeStock(stockId));
	}
}
