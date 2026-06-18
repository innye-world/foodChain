package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.domain.StorageType;
import java.math.BigDecimal;
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
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping({"/type", "/type/"})
	public String productTypeList(Model model) {
		model.addAttribute("productTypes", productService.findAllProductTypes());
		return "product/type/product-type-list";
	}

	@GetMapping("/type/form")
	public String createProductTypeForm() {
		return "product/type/product-type-form";
	}

	@PostMapping(value = "/type", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RedirectView registerProductTypeForm(
			@RequestParam String typeCode, @RequestParam String typeName) {
		productService.registerProductType(typeCode, typeName);
		return new RedirectView("/product/type");
	}

	@GetMapping({"", "/"})
	public String productList(Model model) {
		model.addAttribute("products", productService.findAllProducts());
		return "product/product-list";
	}

	@GetMapping("/form")
	public String createProductForm(Model model) {
		model.addAttribute("productTypes", productService.findAllProductTypes());
		model.addAttribute("storageTypes", StorageType.values());
		return "product/product-form";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RedirectView registerProductForm(
			@RequestParam Long productTypeId,
			@RequestParam String productName,
			@RequestParam StorageType storageType,
			@RequestParam BigDecimal minTemperature,
			@RequestParam BigDecimal maxTemperature,
			@RequestParam(defaultValue = "7") int warningThresholdDays,
			@RequestParam(defaultValue = "20.0") BigDecimal warningThresholdPct) {
		productService.registerProduct(
				productTypeId,
				productName,
				storageType,
				minTemperature,
				maxTemperature,
				warningThresholdDays,
				warningThresholdPct);
		return new RedirectView("/product");
	}

	@GetMapping("/{productId}")
	public String productView(@PathVariable String productId, Model model) {
		model.addAttribute("product", productService.findProduct(productId));
		return "product/product-view";
	}
}
