<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	async function load() {
		const { data } = await rq
			.apiEndPoints()
			.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });
		return data!;
	}
</script>

{#await load()}
	<div>loading...</div>
{:then { data: { item: post } }}
	<h1>{post.title}</h1>
	<p>{post.body}</p>
{/await}
