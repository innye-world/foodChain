(function () {
	const typeSelect = document.getElementById('filterTypeCode');
	const productSelect = document.getElementById('filterProductId');

	if (!typeSelect || !productSelect) {
		return;
	}

	const initialTypeCode = typeSelect.dataset.selected || '';
	const initialProductId = productSelect.dataset.selected || '';

	function resetProductSelect(message) {
		productSelect.innerHTML = '';
		const option = document.createElement('option');
		option.value = '';
		option.textContent = message;
		option.disabled = true;
		option.selected = true;
		productSelect.appendChild(option);
		productSelect.disabled = true;
		productSelect.removeAttribute('name');
	}

	function fillProductSelect(products, selectedProductId) {
		productSelect.innerHTML = '';
		const placeholder = document.createElement('option');
		placeholder.value = '';
		placeholder.textContent = products.length > 0 ? '전체' : '등록된 제품 없음';
		placeholder.selected = !selectedProductId;
		productSelect.appendChild(placeholder);

		products.forEach(function (product) {
			const option = document.createElement('option');
			option.value = product.productCode;
			option.textContent = product.productName + ' (' + product.productCode + ')';
			if (selectedProductId && product.productCode === selectedProductId) {
				option.selected = true;
			}
			productSelect.appendChild(option);
		});

		productSelect.disabled = products.length === 0;
		if (products.length > 0) {
			productSelect.setAttribute('name', 'productId');
		} else {
			productSelect.removeAttribute('name');
		}
	}

	function loadProducts(typeCode, selectedProductId) {
		if (!typeCode) {
			resetProductSelect('먼저 유형을 선택하세요');
			return;
		}

		resetProductSelect('제품 불러오는 중…');

		fetch('/api/products?typeCode=' + encodeURIComponent(typeCode), {
			headers: { Accept: 'application/json' },
		})
			.then(function (response) {
				if (!response.ok) {
					throw new Error('load failed');
				}
				return response.json();
			})
			.then(function (products) {
				fillProductSelect(products, selectedProductId);
			})
			.catch(function () {
				resetProductSelect('제품을 불러올 수 없습니다');
			});
	}

	typeSelect.addEventListener('change', function () {
		loadProducts(typeSelect.value, '');
	});

	resetProductSelect('먼저 유형을 선택하세요');

	if (initialTypeCode) {
		typeSelect.value = initialTypeCode;
		loadProducts(initialTypeCode, initialProductId);
	}
})();
