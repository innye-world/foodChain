(function () {
	const typeSelect = document.getElementById('typeCode');
	const productSelect = document.getElementById('productId');
	const hint = document.getElementById('product-load-hint');
	const form = document.getElementById('stock-form');
	const validationError = document.getElementById('form-validation-error');
	const mfgDateInput = document.getElementById('mfgDate');
	const expiryDateInput = document.getElementById('expiryDate');
	const expiryDateHint = document.getElementById('expiry-date-hint');

	if (!form || !typeSelect || !productSelect) {
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

	function setValidationError(message) {
		if (!validationError) {
			return;
		}
		if (!message) {
			validationError.hidden = true;
			validationError.textContent = '';
			return;
		}
		validationError.hidden = false;
		validationError.textContent = message;
		validationError.style.color = '#b42318';
	}

	function setExpiryDateHint(message) {
		if (!expiryDateHint) {
			return;
		}
		if (!message) {
			expiryDateHint.hidden = true;
			expiryDateHint.textContent = '';
			return;
		}
		expiryDateHint.hidden = false;
		expiryDateHint.textContent = message;
		expiryDateHint.style.color = '#b42318';
	}

	function syncExpiryMinDate() {
		if (!mfgDateInput || !expiryDateInput) {
			return;
		}
		const mfgDate = mfgDateInput.value;
		if (mfgDate) {
			expiryDateInput.min = mfgDate;
			if (expiryDateInput.value && expiryDateInput.value < mfgDate) {
				setExpiryDateHint('유통기한은 제조일자 이후여야 합니다.');
			} else {
				setExpiryDateHint('');
			}
		} else {
			expiryDateInput.removeAttribute('min');
			setExpiryDateHint('');
		}
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

	function validateForm() {
		const productId = productSelect.value;
		const lotNo = document.getElementById('lotNo')?.value.trim();
		const mfgDate = mfgDateInput?.value;
		const expiryDate = expiryDateInput?.value;
		const temperatureRaw = document.getElementById('currentTemperature')?.value;
		const amountRaw = document.getElementById('amount')?.value;

		if (!productId) {
			return '상품을 선택해 주세요.';
		}
		if (!lotNo) {
			return 'LOT 번호를 입력해 주세요.';
		}
		if (!mfgDate) {
			return '제조일자를 입력해 주세요.';
		}
		if (!expiryDate) {
			return '유통기한을 입력해 주세요.';
		}
		if (expiryDate < mfgDate) {
			return '유통기한은 제조일자 이후여야 합니다.';
		}
		if (temperatureRaw === '' || temperatureRaw == null) {
			return '입고 구역 온도를 입력해 주세요.';
		}
		if (Number.isNaN(Number(temperatureRaw))) {
			return '입고 구역 온도는 숫자로 입력해 주세요.';
		}
		if (amountRaw === '' || amountRaw == null) {
			return '수량을 입력해 주세요.';
		}
		const amount = Number(amountRaw);
		if (!Number.isInteger(amount) || amount <= 0) {
			return '입고 수량은 1 이상이어야 합니다.';
		}
		return '';
	}

	resetProductSelect('먼저 유형을 선택하세요');

	if (mfgDateInput) {
		mfgDateInput.addEventListener('change', syncExpiryMinDate);
	}
	if (expiryDateInput) {
		expiryDateInput.addEventListener('change', syncExpiryMinDate);
	}

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

	form.addEventListener('submit', function (event) {
		syncExpiryMinDate();
		const message = validateForm();
		if (message) {
			event.preventDefault();
			setValidationError(message);
			form.reportValidity();
			return;
		}
		setValidationError('');
	});
})();
