<template>
	<div class="page">
		<div class="card">
			<svg class="illustration" viewBox="0 0 320 220" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="工地建设图案">
				<rect x="0" y="190" width="320" height="30" fill="#e5e7eb" />
				<rect x="26" y="146" width="88" height="44" rx="6" fill="#f59e0b" />
				<rect x="42" y="160" width="18" height="12" fill="#111827" />
				<rect x="68" y="160" width="18" height="12" fill="#111827" />
				<circle cx="48" cy="194" r="16" fill="#1f2937" />
				<circle cx="92" cy="194" r="16" fill="#1f2937" />
				<rect x="185" y="42" width="12" height="148" fill="#6b7280" />
				<rect x="190" y="42" width="110" height="10" fill="#9ca3af" />
				<line x1="285" y1="52" x2="285" y2="108" stroke="#6b7280" stroke-width="4" />
				<rect x="274" y="108" width="22" height="14" rx="2" fill="#f59e0b" />
				<rect x="140" y="94" width="48" height="96" fill="#d1d5db" />
				<rect x="146" y="104" width="16" height="16" fill="#93c5fd" />
				<rect x="166" y="104" width="16" height="16" fill="#93c5fd" />
				<rect x="146" y="126" width="16" height="16" fill="#93c5fd" />
				<rect x="166" y="126" width="16" height="16" fill="#93c5fd" />
			</svg>

			<h1>🚧 正在建设中</h1>
			<p>你访问的页面还在施工，敬请期待。</p>
			<router-link to="/" class="back-link">返回首页</router-link>
		</div>
	</div>
</template>

<style scoped>
.page {
	min-height: calc(100vh - 56px);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 24px 16px 120px;
}

.card {
	width: 100%;
	max-width: 420px;
	background: #fff;
	border-radius: 16px;
	padding: 20px;
	text-align: center;
	box-shadow: 0 10px 24px rgba(15, 23, 42, 0.1);
}

.illustration {
	width: 100%;
	max-width: 280px;
	margin: 0 auto 12px;
	display: block;
}

h1 {
	margin: 8px 0;
	font-size: 24px;
	color: #111827;
}

p {
	margin: 0;
	color: #6b7280;
}

.back-link {
	display: inline-block;
	margin-top: 16px;
	color: #2563eb;
	font-weight: 600;
}
</style>