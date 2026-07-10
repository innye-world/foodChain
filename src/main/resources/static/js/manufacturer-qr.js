document.addEventListener('DOMContentLoaded', function () {
	const qrContainer = document.getElementById('qrcode');
	const copyButton = document.getElementById('btn-copy-url');
	const urlInput = document.getElementById('qr-url-text');

	if (qrContainer && qrContainer.dataset.url) {
		renderQrCode(qrContainer, qrContainer.dataset.url);
		window.addEventListener('resize', function () {
			renderQrCode(qrContainer, qrContainer.dataset.url);
		});
	}

	if (copyButton && urlInput) {
		copyButton.addEventListener('click', function () {
			urlInput.select();
			urlInput.setSelectionRange(0, urlInput.value.length);
			navigator.clipboard.writeText(urlInput.value).then(function () {
				copyButton.textContent = '복사됨';
				setTimeout(function () {
					copyButton.textContent = '복사';
				}, 1500);
			}).catch(function () {
				document.execCommand('copy');
			});
		});
	}
});

function renderQrCode(container, text) {
	if (typeof QRCode === 'undefined') {
		return;
	}

	const size = Math.min(260, Math.max(180, window.innerWidth - 96));
	container.innerHTML = '';
	new QRCode(container, {
		text: text,
		width: size,
		height: size,
		correctLevel: QRCode.CorrectLevel.M
	});
}
