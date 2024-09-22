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
			</li>
		{/each}
	</ul>
{/await}
