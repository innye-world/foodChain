package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
