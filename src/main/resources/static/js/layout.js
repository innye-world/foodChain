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

	document.querySelectorAll('.sub-item').forEach((item) => {
		item.classList.remove('active');
		const href = item.getAttribute('href');
		if (!href) {
			return;
		}
		const matched = path === href || (href !== '/' && path.startsWith(href));
		if (!matched) {
			return;
		}

		item.classList.add('active');
		const subNav = item.closest('.sub-nav');
		if (!subNav) {
			return;
		}
		const key = subNav.id.replace('sub-', '');
		subNav.classList.add('open');
		document.getElementById('chevron-' + key)?.classList.add('open');
		subNav.previousElementSibling?.classList.add('active');
	});
}

document.addEventListener('DOMContentLoaded', activateSidebar);
