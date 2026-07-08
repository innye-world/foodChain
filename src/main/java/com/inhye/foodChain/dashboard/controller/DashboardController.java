package com.inhye.foodChain.dashboard.controller;

import com.inhye.foodChain.product.domain.ProductType;
import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ProductService productService;
    private final StockService stockService;

    @GetMapping("/")
    public String dashboard(Model model) {
        // 1. 전체 상품 수
        int allProductCount = productService.findAllProducts().size();
        // 1-1. 이번 주 추가한 상품 수
        int productsAddedThisWeek = productService.countProductsAddedThisWeek();

        // 2. 상품 유형
        List<ProductType> allProductTypes = productService.findAllProductTypes();
        int allProductTypeCount = allProductTypes.size();
        // 2-1. 유형 이름 (2개 쓰고 그 이상은 외 __)
        String typeString = "";
        for(int i = 0; i < allProductTypes.size(); i++){
            if(i >= 2) {
                typeString += " 외 " + (allProductTypeCount-2);
                break;
            }
            if(i > 0) {
                typeString += ", ";
            }
            typeString += allProductTypes.get(i).getTypeName();
        }

        // 3. 총 재고
        int allStockCount = 0;
        List<Stock> allStock = stockService.findAllAvailableAndWarningStocks();
        for(Stock stock : allStock){
            allStockCount += stock.getAmount();
        }
        // 3-1. 오늘 입고 수량
        int stockCountOfToday = stockService.findStockCountOfToday();

        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("allProductCount", allProductCount);
        model.addAttribute("productsAddedThisWeek", productsAddedThisWeek);
        model.addAttribute("allProductTypeCount", allProductTypeCount);
        model.addAttribute("typeString", typeString);
        model.addAttribute("allStockCount", allStockCount);
        model.addAttribute("stockCountOfToday", stockCountOfToday);

        return "dashboard";
    }
}