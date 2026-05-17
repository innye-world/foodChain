package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.service.ProductService;
import com.inhye.foodChain.stock.domain.StorageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
