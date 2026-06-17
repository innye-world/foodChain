package com.inhye.foodChain.stock.repository;

import com.inhye.foodChain.stock.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {}
