package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

	private final StockService stockService;
	private final ProductService productService;

	@GetMapping({"", "/"})
	public String stockList(Model model) {
		model.addAttribute("stocks", stockService.findAllStocksOrderByFefo());
		return "stock/stock-list";
	}

	@GetMapping("/form")
	public String stockForm(Model model) {
		model.addAttribute("productTypes", productService.findAllProductTypes());
		return "stock/stock-form";
	}
}