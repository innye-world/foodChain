(function () {
	const holdActions = document.getElementById('hold-actions');
	if (!holdActions) {
		return;
	}

	const stockId = holdActions.dataset.stockId;
	const releaseBtn = document.getElementById('btn-release');
	const disposeBtn = document.getElementById('btn-dispose');
	const errorEl = document.getElementById('hold-action-error');

	function showError(message) {
		if (!errorEl) {
			return;
		}
		errorEl.hidden = false;
		errorEl.textContent = message;
	}

	function clearError() {
		if (!errorEl) {
			return;
		}
		errorEl.hidden = true;
		errorEl.textContent = '';
	}

	function setButtonsDisabled(disabled) {
		if (releaseBtn) {
			releaseBtn.disabled = disabled;
		}
		if (disposeBtn) {
			disposeBtn.disabled = disabled;
		}
	}

	async function callHoldAction(action, confirmMessage) {
		if (!window.confirm(confirmMessage)) {
			return;
		}

		clearError();
		setButtonsDisabled(true);

		try {
			const response = await fetch('/api/stocks/' + encodeURIComponent(stockId) + '/' + action, {
				method: 'PATCH',
				headers: { Accept: 'application/json' },
			});

			if (!response.ok) {
				let message = '요청에 실패했습니다.';
				try {
					const body = await response.json();
					if (body && body.message) {
						message = body.message;
					}
				} catch (ignored) {
					// ignore parse error
				}
				throw new Error(message);
			}

			window.location.reload();
		} catch (error) {
			showError(error.message || '요청에 실패했습니다.');
			setButtonsDisabled(false);
		}
	}

	if (releaseBtn) {
		releaseBtn.addEventListener('click', function () {
			callHoldAction('release', '품질 확인 후 보류를 해제하시겠습니까?');
		});
	}

	if (disposeBtn) {
		disposeBtn.addEventListener('click', function () {
			callHoldAction('dispose', '이 배치를 폐기하시겠습니까?');
		});
	}
})();
