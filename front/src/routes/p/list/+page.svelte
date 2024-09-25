<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function load() {
		const { data } = await rq.apiEndPoints().GET('/api/v1/posts', {});

		return data!;
	}
</script>

{#await load()}
	<p>loading...</p>
{:then { data: { items: posts } }}
	<h1>글 리스트</h1>
	<ul>
		{#each posts as post}
			<li>
				<a href="/p/{post.id}">{post.title}</a>

				{#if post.actorCanDelete}
					<button onclick={() => rq.confirmAndDeletePost(post, () => rq.reload())}>삭제</button>
				{/if}
				{#if post.actorCanEdit}
					<a href="/p/{post.id}/edit">수정</a>
				{/if}
			</li>
		{/each}
	</ul>
{/await}
