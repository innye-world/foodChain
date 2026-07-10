package com.inhye.foodChain.stock.domain;

public enum StockListSort {

	FEFO,
	RECEIVED;

	public static StockListSort fromParam(String sort) {
		if ("received".equalsIgnoreCase(sort)) {
			return RECEIVED;
		}
		return FEFO;
	}

	public String queryValue() {
		return this == RECEIVED ? "received" : null;
	}
}
