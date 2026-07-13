function toggleNav(key) {
	const sub = document.getElementById('sub-' + key);
	const chev = document.getElementById('chevron-' + key);
	const btn = chev.closest('.nav-btn');
	const isOpen = sub.classList.contains('open');
	sub.classList.toggle('open', !isOpen);
	chev.classList.toggle('open', !isOpen);
	btn.classList.toggle('active', !isOpen);
}

function activateSidebar() {
	const path = window.location.pathname;
	const items = Array.from(document.querySelectorAll('.sub-item'));
	let bestItem = null;
	let bestHrefLength = -1;

	items.forEach((item) => {
		item.classList.remove('active');
		const href = item.getAttribute('href');
		if (!href) {
			return;
		}
		// /stock 이 /stock/history 까지 매칭되지 않도록, 정확히 같거나 href/ 로 이어질 때만 후보
		const matched = path === href || (href !== '/' && path.startsWith(href + '/'));
		if (!matched) {
			return;
		}
		// 여러 후보 중 가장 긴 경로만 활성화 (예: /stock/history > /stock)
		if (href.length > bestHrefLength) {
			bestItem = item;
			bestHrefLength = href.length;
		}
	});

	if (!bestItem) {
		return;
	}

	bestItem.classList.add('active');
	const subNav = bestItem.closest('.sub-nav');
	if (!subNav) {
		return;
	}
	const key = subNav.id.replace('sub-', '');
	subNav.classList.add('open');
	document.getElementById('chevron-' + key)?.classList.add('open');
	subNav.previousElementSibling?.classList.add('active');
}

function initMobileNav() {
	const toggle = document.querySelector('.menu-toggle');
	const sidebar = document.querySelector('.sidebar');
	const overlay = document.getElementById('sidebar-overlay');

	if (!toggle || !sidebar) {
		return;
	}

	function closeSidebar() {
		sidebar.classList.remove('is-open');
		overlay?.classList.remove('is-visible');
		toggle.setAttribute('aria-expanded', 'false');
		overlay?.setAttribute('aria-hidden', 'true');
		document.body.classList.remove('nav-open');
	}

	function openSidebar() {
		sidebar.classList.add('is-open');
		overlay?.classList.add('is-visible');
		toggle.setAttribute('aria-expanded', 'true');
		overlay?.setAttribute('aria-hidden', 'false');
		document.body.classList.add('nav-open');
	}

	toggle.addEventListener('click', function () {
		if (sidebar.classList.contains('is-open')) {
			closeSidebar();
		} else {
			openSidebar();
		}
	});

	overlay?.addEventListener('click', closeSidebar);

	document.querySelectorAll('.sub-item, .sidebar-logo').forEach(function (el) {
		el.addEventListener('click', function () {
			if (window.matchMedia('(max-width: 768px)').matches) {
				closeSidebar();
			}
		});
	});

	window.addEventListener('resize', function () {
		if (window.matchMedia('(min-width: 769px)').matches) {
			closeSidebar();
		}
	});
}

document.addEventListener('DOMContentLoaded', function () {
	activateSidebar();
	initMobileNav();
});
