package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.dto.StockMovementResponse;
import com.inhye.foodChain.stock.service.StockService;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public String stockList(@RequestParam(defaultValue = "0") int page, Model model) {
		model.addAttribute("page", stockService.findStocksPage(page));
		return "stock/stock-list";
	}

	@GetMapping("/history")
	public String stockMovementList(@RequestParam(defaultValue = "0") int page, Model model) {
		model.addAttribute("page", stockService.findMovementsPage(page).map(StockMovementResponse::from));
		return "stock/stock-movement-list";
	}

	@GetMapping("/form")
	public String stockForm(Model model) {
		model.addAttribute("productTypes", productService.findAllProductTypes());
		return "stock/stock-form";
	}

	@GetMapping("/{stockId}")
	public String stockDetail(@PathVariable Long stockId, Model model) {
		model.addAttribute("stock", stockService.findStockById(stockId));
		return "stock/stock-detail";
	}

	/**
	 * 	Swagger도 겸용으로 사용할 수 있도록 thymeleaf에 연결한 컨트롤러도 추가 (추후 제거 예정)
	 * @param productId
	 * @param lotNo
	 * @param mfgDate
	 * @param expiryDate
	 * @param amount
	 * @param currentTemperature
	 * @return
	 */
	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RedirectView registerStockForm(
			@RequestParam String productId,
			@RequestParam String lotNo,
			@RequestParam LocalDate mfgDate,
			@RequestParam LocalDate expiryDate,
			@RequestParam int amount,
			@RequestParam BigDecimal currentTemperature) {
		stockService.registerStock(productId, lotNo, mfgDate, expiryDate, amount, currentTemperature);
		return new RedirectView("/stock");
	}
}