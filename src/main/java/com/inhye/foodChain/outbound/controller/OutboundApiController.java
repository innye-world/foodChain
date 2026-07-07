package com.inhye.foodChain.outbound.controller;

import com.inhye.foodChain.outbound.dto.OutboundDetail;
import com.inhye.foodChain.outbound.dto.OutboundOrderRequest;
import com.inhye.foodChain.outbound.dto.OutboundOrderResponse;
import com.inhye.foodChain.outbound.service.OutboundService;
import com.inhye.foodChain.stock.dto.StockResponse;
import com.inhye.foodChain.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "출고", description = "출고 요청 및 응답 처리 API. 유통기한이 가장 빠른 순으로 처리합니다.")
@RestController
@RequestMapping("/api/outbound")
@RequiredArgsConstructor
public class OutboundApiController {
    private final OutboundService outboundService;

    @Operation(
            summary = "출고 요청",
            description = "외부로부터 받은 출고 요청을 처리합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "출고 결과 리턴",
                    content = @Content(schema = @Schema(implementation = OutboundOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 오류 또는 재고 부족"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "503", description = "데이터베이스 연결 불가"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public OutboundOrderResponse processOutboundOrder(@Valid @RequestBody OutboundOrderRequest request) {
        return outboundService.processOutboundOrder(request);
    }
}
