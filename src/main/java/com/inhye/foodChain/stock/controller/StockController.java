package com.inhye.foodChain.stock.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.domain.StockListSort;
import com.inhye.foodChain.stock.dto.StockMovementResponse;
import com.inhye.foodChain.stock.service.StockService;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
	public String stockList(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String typeCode,
			@RequestParam(required = false) String productId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String sort,
			Model model) {
		var listSort = StockListSort.fromParam(sort);
		model.addAttribute("page", stockService.findStocksPage(page, typeCode, productId, status, listSort));
		model.addAttribute("productTypes", productService.findAllProductTypes());
		model.addAttribute("selectedTypeCode", typeCode != null ? typeCode : "");
		model.addAttribute("selectedProductId", productId != null ? productId : "");
		model.addAttribute("selectedStatus", status != null ? status : "");
		model.addAttribute("sort", listSort.queryValue() != null ? listSort.queryValue() : "");
		model.addAttribute("filterQuery", buildFilterQuery(typeCode, productId, status, listSort));
		model.addAttribute("listReturnQuery", buildListReturnQuery(typeCode, productId, status, page, listSort));
		return "stock/stock-list";
	}

	private static String buildFilterQuery(
			String typeCode, String productId, String status, StockListSort sort) {
		StringBuilder query = new StringBuilder();
		appendQueryParam(query, "typeCode", typeCode);
		appendQueryParam(query, "productId", productId);
		appendQueryParam(query, "status", status);
		appendQueryParam(query, "sort", sort.queryValue());
		return query.toString();
	}

	private static String buildListReturnQuery(
			String typeCode, String productId, String status, int page, StockListSort sort) {
		StringBuilder query = new StringBuilder(buildFilterQuery(typeCode, productId, status, sort));
		if (page > 0) {
			if (!query.isEmpty()) {
				query.append('&');
			}
			query.append("page=").append(page);
		}
		return query.toString();
	}

	private static void appendQueryParam(StringBuilder query, String name, String value) {
		if (value == null || value.isBlank()) {
			return;
		}
		if (!query.isEmpty()) {
			query.append('&');
		}
		query.append(name)
				.append('=')
				.append(URLEncoder.encode(value.trim(), StandardCharsets.UTF_8));
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

	@GetMapping("/add-temperature")
	public String addTemperatureForInbound(
			@RequestParam String productId,
			@RequestParam LocalDate mfgDate,
			@RequestParam LocalDate expiryDate,
			@RequestParam int amount,
			Model model) {
		model.addAttribute("product", productService.findProduct(productId));
		model.addAttribute("productId", productId);
		model.addAttribute("lotNo", stockService.generateLotNo(productId));
		model.addAttribute("mfgDate", mfgDate);
		model.addAttribute("expiryDate", expiryDate);
		model.addAttribute("amount", amount);
		return "stock/add-temperature";
	}

	@GetMapping("/{stockId}")
	public String stockDetail(
			@PathVariable Long stockId,
			@RequestParam(required = false) String typeCode,
			@RequestParam(required = false) String productId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String sort,
			@RequestParam(defaultValue = "0") int page,
			Model model) {
		model.addAttribute("stock", stockService.findStockById(stockId));
		model.addAttribute(
				"listReturnQuery",
				buildListReturnQuery(typeCode, productId, status, page, StockListSort.fromParam(sort)));
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
		return new RedirectView("/stock?sort=received");
	}
}