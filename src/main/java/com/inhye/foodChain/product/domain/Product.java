package com.inhye.foodChain.product.domain;

import com.inhye.foodChain.stock.domain.StorageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product {

	@Id
	@Column(name = "product_id", length = 20, nullable = false)
	private String productId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_type_id", nullable = false)
	private ProductType productType;

	@Column(name = "product_name", length = 100, nullable = false)
	private String productName;

	@Enumerated(EnumType.STRING)
	@Column(name = "storage_type", length = 15, nullable = false)
	private StorageType storageType;

	@Column(name = "min_temperature", precision = 4, scale = 1, nullable = false)
	private BigDecimal minTemperature;

	@Column(name = "max_temperature", precision = 4, scale = 1, nullable = false)
	private BigDecimal maxTemperature;
}
