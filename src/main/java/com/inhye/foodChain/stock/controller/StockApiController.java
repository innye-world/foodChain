package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockApiController {

	private final StockService stockService;

	@PostMapping
	public RedirectView registerStockForm(
			@RequestParam String productId,
			@RequestParam String lotNo,
			@RequestParam LocalDate mfgDate,
			@RequestParam LocalDate expiryDate,
			@RequestParam int amount
			) {
		stockService.registerStock(productId, lotNo, mfgDate, expiryDate, amount);
		return new RedirectView("/stock");
	}
}
