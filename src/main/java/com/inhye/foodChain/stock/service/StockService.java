package com.inhye.foodChain.stock.service;

import com.inhye.foodChain.stock.domain.Stock;
import com.inhye.foodChain.stock.repository.StockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;

	@Transactional(readOnly = true)
	public List<Stock> findAll() {
		return stockRepository.findAll();
	}
}
