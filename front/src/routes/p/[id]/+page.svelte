<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';

	async function load() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const { data, error } = await rq
			.apiEndPoints()
			.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });

		if (error) throw error;

		return data!;
	}
</script>

{#await load()}
	<div>loading...</div>
{:then { data: { item: post } }}
	<h1>{post.title}</h1>
	<div>추천 : {post.likesCount}</div>

	{#key post.id}
		<ToastUiEditor body={post.body} viewer={true} />
	{/key}

	<div>
		{#if post.actorCanDelete}
			<button onclick={() => rq.confirmAndDeletePost(post, '/p/list')}>삭제</button>
		{/if}

		{#if post.actorCanEdit}
			<a href="/p/{post.id}/edit">수정</a>
		{/if}
	</div>
{:catch error}
	{error.msg}
{/await}
