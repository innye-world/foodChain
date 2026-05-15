package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.service.StockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

	private final StockService stockService;

	@GetMapping
	public List<Stock> findAll() {
		return stockService.findAll();
	}
}
