<script lang="ts">
	import { onMount } from 'svelte';
	import { page } from '$app/stores';

	import '$lib/app.css';

	import rq from '$lib/rq/rq.svelte';

	const { children } = $props();

	onMount(() => {
		rq.initAuth();
	});
</script>

<header class="navbar bg-base-100">
	<div class="flex-none">
		<div class="dropdown">
			<div tabindex="0" role="button" class="btn btn-ghost btn-circle">
				<svg
					xmlns="http://www.w3.org/2000/svg"
					class="h-5 w-5"
					fill="none"
					viewBox="0 0 24 24"
					stroke="currentColor"
					><path
						stroke-linecap="round"
						stroke-linejoin="round"
						stroke-width="2"
						d="M4 6h16M4 12h16M4 18h7"
					/></svg
				>
			</div>
			<!-- svelte-ignore a11y-no-noninteractive-tabindex -->
			<ul
				tabindex="0"
				class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52"
			>
				<li><a href="/p/list"><i class="fa-solid fa-list"></i> 글</a></li>
				{#if rq.isAdmPage($page)}
					<li><a href="/"><i class="fa-solid fa-house"></i> 홈</a></li>
				{/if}
				{#if rq.isUsrPage($page) && rq.isAdmin()}
					<li><a href="/adm"><i class="fa-solid fa-gauge"></i> 관리자</a></li>
				{/if}
			</ul>
		</div>
	</div>

	<div class="flex-1">
		{#if rq.isUsrPage($page)}
			<a href="/" class="btn btn-ghost text-md">SLOG</a>
		{/if}
		{#if rq.isAdmPage($page)}
			<a href="/adm" class="btn btn-ghost text-md">SLOG ADMIN</a>
		{/if}
	</div>

	<div class="flex-none">
		<div class="dropdown dropdown-end">
			{#if rq.isLogout()}
				<button class="btn btn-square btn-ghost">
					<svg
						xmlns="http://www.w3.org/2000/svg"
						fill="none"
						viewBox="0 0 24 24"
						class="inline-block w-5 h-5 stroke-current"
						><path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"
						></path></svg
					>
				</button>
			{/if}
			{#if rq.isLogin()}
				<div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
					<div class="w-10 rounded-full">
						<img src={rq.member.profileImgUrl} alt="" />
					</div>
				</div>
			{/if}
			<!-- svelte-ignore a11y-no-noninteractive-tabindex -->
			<ul
				tabindex="0"
				class="mt-3 z-[1] p-2 shadow menu menu-sm dropdown-content bg-base-100 rounded-box w-52"
			>
				{#if rq.isLogout()}
					<li>
						<a href="/member/login"><i class="fa-solid fa-right-to-bracket"></i> 로그인 & 가입</a>
					</li>
				{/if}
				{#if rq.isLogin()}
					<li>
						<a href="/member/me"><i class="fa-solid fa-user"></i> {rq.member.name}</a>
					</li>
					<li>
						<button onclick={() => rq.goToTempPostEditPage()}>
							<i class="fa-solid fa-pen"></i> 글 쓰기
						</button>
					</li>
					<li>
						<button onclick={() => rq.logoutAndRedirect('/')}
							><i class="fa-solid fa-right-from-bracket"></i> 로그아웃</button
						>
					</li>
				{/if}
			</ul>
		</div>
	</div>
</header>
<main class="flex-grow flex flex-col">{@render children()}</main>
<footer></footer>
