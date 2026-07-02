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
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @ApiResponse(
            responseCode = "200",
            description = "출고 결과 리턴",
            content = @Content(schema = @Schema(implementation = OutboundOrderResponse.class)))
    @PostMapping
    public OutboundOrderResponse processOutboundOrder(@RequestBody OutboundOrderRequest request) {
        return outboundService.processOutboundOrder(request);
    }
}
