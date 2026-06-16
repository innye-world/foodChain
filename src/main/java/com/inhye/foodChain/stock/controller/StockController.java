package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.service.StockService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

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

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RedirectView registerStockForm(
			@RequestParam String productId,
			@RequestParam String lotNo,
			@RequestParam LocalDate mfgDate,
			@RequestParam LocalDate expiryDate,
			@RequestParam int amount) {
		stockService.registerStock(productId, lotNo, mfgDate, expiryDate, amount);
		return new RedirectView("/stock");
	}
}