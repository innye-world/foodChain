package com.inhye.foodChain.product.controller;

import com.inhye.foodChain.product.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product/types")
@RequiredArgsConstructor
public class ProductTypePageController {

	private final ProductTypeService productTypeService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("productTypes", productTypeService.findAll());
		return "product/types/product-list";
	}
}
