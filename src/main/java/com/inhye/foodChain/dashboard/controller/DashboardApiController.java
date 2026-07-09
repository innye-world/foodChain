package com.inhye.foodChain.dashboard.controller;

import com.inhye.foodChain.dashboard.dto.InoutChartResponse;
import com.inhye.foodChain.dashboard.dto.StockRatioByProductResponse;
import com.inhye.foodChain.stock.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대시보드", description = "입출고 현황, 상품 유형별 재고 비율 그래프를 그립니다.")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {
    private final StockService stockService;

    @GetMapping("/inout-chart")
    public InoutChartResponse getInoutChart() {
        return stockService.getInoutChartData();
    }

    @GetMapping("/product-type-stock-ratio")
    public StockRatioByProductResponse getProductTypeStockRatio() {
        return stockService.getStockRatioByProductTypeData();
    }
}