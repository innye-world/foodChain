package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.service.ProductService;
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
	public String list(Model model) {
		model.addAttribute("productTypes", productService.findAllProductTypes());
		return "product/type/product-type-list";
	}

	@GetMapping("/type/form")
	public String createForm() {
		return "product/type/product-type-form";
	}
}
