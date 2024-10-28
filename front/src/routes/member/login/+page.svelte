<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
</script>

<div class="flex-grow flex items-center justify-center">
	<a href={rq.getGoogleLoginUrl()} class="btn">
		<i class="fa-solid fa-comment text-[color:#ffe812]"></i>
		구글로그인으로 3초만에 시작하기
	</a>
</div>
