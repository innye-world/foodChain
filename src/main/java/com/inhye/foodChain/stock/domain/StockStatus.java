package com.inhye.foodChain.stock.domain;

public enum StockStatus {
	PENDING_INSPECTION,
	AVAILABLE,
	WARNING,
	HOLD,
	EXPIRED,
	DISPOSED;

	public boolean canTransitionTo(StockStatus next) {
		return switch (this) {
			case PENDING_INSPECTION -> next == AVAILABLE || next == HOLD;
			case AVAILABLE -> next == HOLD || next == WARNING || next == EXPIRED;
			case WARNING -> next == HOLD || next == EXPIRED;
			case HOLD -> next == AVAILABLE || next == DISPOSED;
			case EXPIRED, DISPOSED -> false;
		};
	}
}
