package com.inhye.foodChain.outbound.repository;

import com.inhye.foodChain.outbound.dto.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {

	boolean existsByOrderId(String orderId);
}
