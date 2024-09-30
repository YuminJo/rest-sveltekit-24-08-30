<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import type { components } from '$lib/types/api/v1/schema';

	let posts: components['schemas']['PostDto'][] = $state([]);

	async function load() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const { data } = await rq.apiEndPoints().GET('/api/v1/posts', {});

		posts = data!.data.items;

		return data!;
	}
</script>

{#await load()}
	<p>loading...</p>
{:then { }}
	<h1>글 리스트</h1>
	<ul>
		{#each posts as post}
			<li>
				<a href="/p/{post.id}">{post.id}. {post.title} / 추천 : {post.likesCount}</a>

				{#if post.actorCanDelete}
					<button
						onclick={() =>
							rq.confirmAndDeletePost(post, () => {
								posts.splice(posts.indexOf(post), 1);
							})}>삭제</button
					>
				{/if}

				{#if post.actorCanEdit}
					<a href="/p/{post.id}/edit">수정</a>
				{/if}

				{#if post.actorCanLike}
					<button
						onclick={() =>
							rq.like(post, (data) => {
								Object.assign(post, data.data.item);
								rq.msgInfo(data.msg);
							})}>추천</button
					>
				{/if}
				{#if post.actorCanCancelLike}
					<button
						onclick={() =>
							rq.cancelLike(post, (data) => {
								Object.assign(post, data.data.item);
								rq.msgInfo(data.msg);
							})}>추천취소</button
					>
				{/if}
			</li>
		{/each}
	</ul>
{/await}
