<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	async function load() {
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
	<div class="whitespace-pre-line">{post.body}</div>
{:catch error}
	{error.msg}
{/await}
