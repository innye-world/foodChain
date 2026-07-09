package com.inhye.foodChain.dashboard.dto;

import java.util.List;

public record InoutChartResponse(
		List<String> labels,
		List<Dataset> datasets) {

	public record Dataset(
			String label,
			List<Integer> data,
			String backgroundColor) {
	}
}
