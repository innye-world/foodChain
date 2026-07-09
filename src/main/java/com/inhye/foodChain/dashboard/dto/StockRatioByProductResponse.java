package com.inhye.foodChain.dashboard.dto;

import java.util.List;

public record StockRatioByProductResponse(
		List<String> labels,
		List<Dataset> datasets) {

	public record Dataset(
			List<Integer> data,
			List<String> backgroundColor) {
	}
}
