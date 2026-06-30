package com.inhye.foodChain.outbound.dto;

import com.inhye.foodChain.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 상품 단위 출고 요청 (헤더). LOT별 실제 출고는 {@code OutboundDetail}에서 관리. */
@Entity
@Table(
		name = "outbound_order",
		indexes = @Index(name = "idx_outbound_order_product_created", columnList = "product_id, created_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OutboundOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "outbound_id")
	private Long outboundId;

	@Column(name = "order_id", nullable = false)
	private String orderId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "request_amount", nullable = false)
	private int requestAmount;

	@Column(name = "fulfilled_amount", nullable = false)
	@Builder.Default
	private int fulfilledAmount = 0;

	@Column(name = "requested_by", length = 50)
	private String requestedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public String getProductId() {
		return product.getProductId();
	}

	public void addFulfilledAmount(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("출고 수량은 0보다 커야 합니다");
		}
		if (fulfilledAmount + quantity > requestAmount) {
			throw new IllegalStateException("출고 수량이 요청 수량을 초과합니다");
		}
		fulfilledAmount += quantity;
	}
}
