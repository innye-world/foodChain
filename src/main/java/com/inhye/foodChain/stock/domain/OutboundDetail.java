package com.inhye.foodChain.stock.domain;

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

/** 출고 요청({@link OutboundOrder})별 LOT 단위 실제 출고 내역. */
@Entity
@Table(
		name = "outbound_detail",
		indexes = @Index(name = "idx_outbound_detail_order_created", columnList = "outbound_id, created_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OutboundDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "outbound_detail_id")
	private Long outboundDetailId;

	/** 출고 요청 헤더 (outbound_id FK). */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "outbound_id", nullable = false)
	private OutboundOrder outboundOrder;

	/** 출고 대상 재고 LOT (stock_id FK). */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	/** 이 LOT에서 출고한 수량. */
	@Column(name = "outbound_amount", nullable = false)
	private int outboundAmount;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public Long getOutboundId() {
		return outboundOrder.getOutboundId();
	}

	public Long getStockId() {
		return stock.getStockId();
	}

	/** LOT 번호. {@code outbound_detail}에는 저장하지 않고 {@link Stock#getLotNo()}로 조회. */
	public String getLotNo() {
		return stock.getLotNo();
	}
}
