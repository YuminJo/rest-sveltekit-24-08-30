<script lang="ts">
	import createClient from 'openapi-fetch';
	import type { paths } from '$lib/types/api/v1/schema';

	const { GET } = createClient<paths>({ baseUrl: 'http://localhost:8090/' });

	async function load() {
		const { data } = await GET('/api/v1/posts', {});

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
