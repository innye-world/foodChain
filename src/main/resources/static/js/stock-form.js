(function () {
	const typeSelect = document.getElementById('typeCode');
	const productSelect = document.getElementById('productId');
	const hint = document.getElementById('product-load-hint');

	if (!typeSelect || !productSelect) {
		return;
	}

	function setHint(message, isError) {
		if (!hint) {
			return;
		}
		if (!message) {
			hint.hidden = true;
			hint.textContent = '';
			return;
		}
		hint.hidden = false;
		hint.textContent = message;
		hint.style.color = isError ? '#b42318' : '';
	}

	function resetProductSelect(message) {
		productSelect.innerHTML = '';
		const option = document.createElement('option');
		option.value = '';
		option.textContent = message;
		option.disabled = true;
		option.selected = true;
		productSelect.appendChild(option);
		productSelect.disabled = true;
		productSelect.removeAttribute('required');
	}

	function fillProductSelect(products) {
		productSelect.innerHTML = '';
		const placeholder = document.createElement('option');
		placeholder.value = '';
		placeholder.textContent = products.length > 0 ? '상품 선택' : '등록된 상품 없음';
		placeholder.disabled = true;
		placeholder.selected = true;
		productSelect.appendChild(placeholder);

		products.forEach(function (product) {
			const option = document.createElement('option');
			option.value = product.productCode;
			option.textContent = product.productName;
			productSelect.appendChild(option);
		});

		productSelect.disabled = products.length === 0;
		if (products.length > 0) {
			productSelect.setAttribute('required', 'required');
		}
	}

	resetProductSelect('먼저 유형을 선택하세요');

	typeSelect.addEventListener('change', function () {
		const typeCode = typeSelect.value;
		setHint('');

		if (!typeCode) {
			resetProductSelect('먼저 유형을 선택하세요');
			return;
		}

		resetProductSelect('상품 불러오는 중…');
		productSelect.disabled = true;

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
				fillProductSelect(products);
				if (products.length === 0) {
					setHint('선택한 유형에 등록된 상품이 없습니다. 상품 관리에서 먼저 등록하세요.');
				}
			})
			.catch(function () {
				resetProductSelect('상품을 불러올 수 없습니다');
				setHint('상품 목록을 가져오지 못했습니다. 잠시 후 다시 시도하세요.', true);
			});
	});
})();
