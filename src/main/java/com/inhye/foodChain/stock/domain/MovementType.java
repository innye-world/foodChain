package com.inhye.foodChain.stock.domain;

public enum MovementType {
	INBOUND,
	OUTBOUND,
	HOLD,
	RELEASE,
	DISPOSAL,
	ADJUSTMENT;

	public String getDisplayName() {
		return switch (this) {
			case INBOUND -> "입고";
			case OUTBOUND -> "출고";
			case HOLD -> "보류";
			case RELEASE -> "보류 해제";
			case DISPOSAL -> "폐기";
			case ADJUSTMENT -> "상태 변경";
		};
	}
}
