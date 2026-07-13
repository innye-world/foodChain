document.addEventListener('DOMContentLoaded', function () {
	const form = document.getElementById('inbound-temperature-form');
	const temperatureInput = document.getElementById('currentTemperature');
	const signToggle = document.getElementById('tempSignToggle');
	const submitButton = form ? form.querySelector('.inbound-submit') : null;
	let isNegative = false;

	if (!form || !temperatureInput) {
		return;
	}

	if (signToggle) {
		signToggle.addEventListener('click', function () {
			isNegative = !isNegative;
			updateSignToggle();
			temperatureInput.focus();
		});
	}

	temperatureInput.focus();

	form.addEventListener('submit', function (event) {
		event.preventDefault();

		const temperature = parseTemperature(temperatureInput.value, isNegative);
		const message = validateTemperature(temperature);
		if (message) {
			showError(message);
			temperatureInput.focus();
			return;
		}
		hideError();

		if (submitButton) {
			submitButton.disabled = true;
			submitButton.textContent = '저장 중…';
		}

		fetch('/api/stocks', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				Accept: 'application/json'
			},
			body: JSON.stringify({
				productId: document.getElementById('productId').value,
				lotNo: document.getElementById('lotNo').value,
				mfgDate: document.getElementById('mfgDate').value,
				expiryDate: document.getElementById('expiryDate').value,
				amount: Number(document.getElementById('amount').value),
				currentTemperature: temperature,
				inboundToken: document.getElementById('inboundToken').value
			})
		})
			.then(function (response) {
				if (response.ok) {
					window.location.href = '/stock?sort=received';
					return null;
				}
				return response.json().then(function (body) {
					throw new Error(body && body.message ? body.message : '입고에 실패했습니다.');
				});
			})
			.catch(function (error) {
				showError(error.message || '입고에 실패했습니다.');
				if (submitButton) {
					submitButton.disabled = false;
					submitButton.textContent = '입고 저장';
				}
			});
	});

	temperatureInput.addEventListener('input', function () {
		hideError();
		const raw = temperatureInput.value.trim().replace(',', '.');
		if (raw.startsWith('-')) {
			isNegative = true;
			updateSignToggle();
			temperatureInput.value = raw.slice(1);
		}
	});

	function updateSignToggle() {
		if (!signToggle) {
			return;
		}
		signToggle.textContent = isNegative ? '−' : '+';
		signToggle.classList.toggle('is-negative', isNegative);
		signToggle.setAttribute(
			'aria-label',
			isNegative ? '온도 부호, 현재 음수' : '온도 부호, 현재 양수'
		);
	}
});

function parseTemperature(value, isNegative) {
	const raw = (value || '').trim().replace(',', '.');
	if (!raw) {
		return null;
	}
	const num = Number(raw);
	if (Number.isNaN(num)) {
		return NaN;
	}
	if (isNegative) {
		return -Math.abs(num);
	}
	return num;
}

function validateTemperature(temperature) {
	if (temperature == null) {
		return '입고 구역 온도를 입력해 주세요.';
	}
	if (Number.isNaN(temperature)) {
		return '온도는 숫자로 입력해 주세요.';
	}
	return '';
}

function showError(message) {
	const errorEl = document.getElementById('inbound-form-error');
	if (!errorEl) {
		return;
	}
	errorEl.hidden = false;
	errorEl.textContent = message;
}

function hideError() {
	const errorEl = document.getElementById('inbound-form-error');
	if (!errorEl) {
		return;
	}
	errorEl.hidden = true;
	errorEl.textContent = '';
}
