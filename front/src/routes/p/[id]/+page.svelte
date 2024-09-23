<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	async function load() {
		const { data, error } = await rq
			.apiEndPoints()
			.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });

		// 이 코드가 실행되면 아래에 `{:catch error}` 부분으로 넘어감
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
	<!-- .msg 로 접근할 수 있는 이유는 스프링부트의 에러관련 출력을 커스터마이징 했기 때문 -->
	{error.msg}
{/await}
