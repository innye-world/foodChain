package com.inhye.foodChain.stock.domain;

import com.inhye.foodChain.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "stock",
		uniqueConstraints = @UniqueConstraint(name = "uk_stock_product_lot", columnNames = {"product_id", "lot_no"}),
		indexes = {
				@Index(name = "idx_stock_product_fefo", columnList = "product_id, expiry_date, received_at"),
				@Index(name = "idx_stock_status", columnList = "stock_status")
		})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stock_id")
	private Long stockId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "lot_no", length = 20, nullable = false)
	private String lotNo;

	@Column(name = "mfg_date", nullable = false)
	private LocalDate mfgDate;

	@Column(name = "expiry_date", nullable = false)
	private LocalDate expiryDate;

	/** 최초 입고 시각 (FEFO 2순위, 현황 목록 표시) */
	@Column(name = "received_at", nullable = false, updatable = false)
	private LocalDateTime receivedAt;

	@Column(nullable = false)
	private int amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "stock_status", length = 20, nullable = false)
	private StockStatus stockStatus;

	@PrePersist
	void prePersist() {
		if (receivedAt == null) {
			receivedAt = LocalDateTime.now();
		}
	}

	public String getProductId() {
		return product.getProductId();
	}

	public String getProductName() {
		return product.getProductName();
	}

	public void updateStatus(StockStatus next) {
		if (!stockStatus.canTransitionTo(next)) {
			throw new IllegalStateException(
					"재고 상태를 변경할 수 없습니다: " + stockStatus + " → " + next);
		}
		stockStatus = next;
	}
}
