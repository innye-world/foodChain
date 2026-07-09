function initDashboardCharts() {
	if (typeof Chart === 'undefined') {
		console.error('Chart.js를 불러오지 못했습니다.');
		return;
	}

	const inoutEl = document.getElementById('inoutChart');
	const typeEl = document.getElementById('typeChart');
	if (!inoutEl || !typeEl) {
		console.error('차트 canvas를 찾을 수 없습니다.');
		return;
	}

	const charts = [];

	Promise.all([loadInoutChartData(), loadProductTypeRatioData()])
		.then(function (results) {
			charts.push(new Chart(inoutEl, {
				type: 'bar',
				data: results[0],
				options: {
					responsive: true,
					maintainAspectRatio: false,
					scales: {
						y: { beginAtZero: true }
					}
				}
			}));

			charts.push(new Chart(typeEl, {
				type: 'doughnut',
				data: results[1],
				options: {
					responsive: true,
					maintainAspectRatio: false
				}
			}));

			function resizeCharts() {
				charts.forEach(function (chart) {
					chart.resize();
				});
			}

			window.addEventListener('resize', resizeCharts);
			requestAnimationFrame(resizeCharts);
		})
		.catch(function () {
			console.error('차트 데이터를 불러올 수 없습니다.');
		});
}

function loadInoutChartData() {
	return fetch('/api/dashboard/inout-chart', {
		headers: { Accept: 'application/json' }
	}).then(function (response) {
		if (!response.ok) {
			throw new Error('load failed');
		}
		return response.json();
	});
}

function loadProductTypeRatioData() {
	return fetch('/api/dashboard/product-type-stock-ratio', {
		headers: { Accept: 'application/json' }
	}).then(function (response) {
		if (!response.ok) {
			throw new Error('load failed');
		}
		return response.json();
	});
}

if (document.readyState === 'loading') {
	document.addEventListener('DOMContentLoaded', initDashboardCharts);
} else {
	initDashboardCharts();
}
