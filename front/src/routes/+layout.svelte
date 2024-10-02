<script lang="ts">
	import { onMount } from 'svelte';

	import '$lib/app.css';

	import rq from '$lib/rq/rq.svelte';

	const { children } = $props();

	onMount(() => {
		rq.initAuth();
	});
</script>

<header>
	<a href="/">slog</a>

	{#if rq.isLogin()}
		<img class="inline-block rounded" src={rq.member.profileImgUrl} width="30" alt="" />
		<a href="/member/me">{rq.member.name}님의 정보</a>
		<button on:click={() => rq.logoutAndRedirect('/')}>로그아웃</button>
	{/if}

	{#if rq.isLogout()}
		<a href="/member/login">로그인</a>
		<a href="/member/join">회원가입</a>
	{/if}
	<a href="/p/list">글</a>
</header>
<main>{@render children()}</main>
<footer>푸터</footer>
