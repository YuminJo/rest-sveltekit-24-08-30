<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import ut from '$lib/util/ut';
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
	<ul class="grid grid-cols-1 gap-3">
		{#each posts as post}
			<li>
				<div class="flex items-center gap-2">
					<a href="/p/{post.id}">{post.id}. {post.title}</a>
					<span>추천 : {post.likesCount}</span>
					<span>작성일 : {ut.prettyDateTime(post.createDate)}</span>

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
								})}>추천하기</button
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

					<span class="flex-1"></span>

					<span>작성자 : {post.authorName}</span>
					<img src={post.authorProfileImgUrl} width="50" class="rounded-full" alt="" />
				</div>
			</li>
		{/each}
	</ul>
{/await}
