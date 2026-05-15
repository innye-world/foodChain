package com.inhye.foodChain.product.service;

import com.inhye.foodChain.product.domain.ProductType;
import com.inhye.foodChain.product.repository.ProductTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

	private final ProductTypeRepository productTypeRepository;

	@Transactional(readOnly = true)
	public List<ProductType> findAll() {
		return productTypeRepository.findAll(Sort.by("typeCode"));
	}
}
