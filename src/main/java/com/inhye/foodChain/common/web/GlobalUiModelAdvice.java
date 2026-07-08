package com.inhye.foodChain.common.web;

import com.inhye.foodChain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalUiModelAdvice {

	private final StockService stockService;

	@ModelAttribute
	public void addGlobalAttributes(Model model) {
		model.addAttribute("stockCountOfToday", stockService.findStockCountOfToday());
	}

}
