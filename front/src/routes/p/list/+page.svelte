<script lang="ts">
	import type { components } from '$lib/types/api/v1/schema';

	async function load() {
		return (await fetch('http://localhost:8090/api/v1/posts').then((res) =>
			res.json()
		)) as components['schemas']['RsDataGetPostsResponseBody'];
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
