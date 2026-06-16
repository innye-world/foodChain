package com.inhye.foodChain.stock.domain;

/**
 * 재고 상태. 입고 시점에는 자가품질검사가 완료된 상품만 등록한다 ({@link #AVAILABLE}).
 */
public enum StockStatus {
	AVAILABLE,
	WARNING,
	HOLD,
	EXPIRED,
	DISPOSED;

	public boolean canTransitionTo(StockStatus next) {
		return switch (this) {
			case AVAILABLE -> next == HOLD || next == WARNING || next == EXPIRED;
			case WARNING -> next == HOLD || next == EXPIRED;
			case HOLD -> next == AVAILABLE || next == DISPOSED;
			case EXPIRED, DISPOSED -> false;
		};
	}
}
