package com.inhye.foodChain.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "product_type",
		uniqueConstraints = @UniqueConstraint(name = "uk_product_type_code", columnNames = "type_code"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_type_id")
	private Long productTypeId;

	@Column(name = "type_code", length = 20, nullable = false)
	private String typeCode;

	@Column(name = "type_name", length = 50, nullable = false)
	private String typeName;

	@Column(nullable = false)
	@Builder.Default
	private int seq = 0;

	public String issueNextProductId() {
		seq++;
		return formatProductId(seq);
	}

	/** 현재까지 발급된 마지막 상품 ID (미발급 시 '-') */
	public String currentProductId() {
		return seq == 0 ? "-" : formatProductId(seq);
	}

	private String formatProductId(int sequence) {
		return typeCode + "-" + String.format("%03d", sequence);
	}
}
