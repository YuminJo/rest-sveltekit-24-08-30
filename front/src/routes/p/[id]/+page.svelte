<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';
	import { prettyDate, prettyDateTime } from '$lib/utils';

	async function load() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const [postData, commentsData] = await Promise.all([
			rq
				.apiEndPoints()
				.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } }),
			rq.apiEndPoints().GET('/api/v1/postComments/{postId}', {
				params: { path: { postId: parseInt($page.params.id) } }
			})
		]);

		if (postData.error) throw postData.error;
		if (commentsData.error) throw commentsData.error;

		return { postData: postData.data!, commentsData: commentsData.data! };
	}
</script>

{#await load()}
	<div>loading...</div>
{:then { postData: { data: { item: post } }, commentsData: { data: { items: postComments } } }}
	<h1>{post.title}</h1>
	<div>추천 : {post.likesCount}</div>

	{#key post.id}
		<ToastUiEditor body={post.body} viewer={true} />
	{/key}

	<h1 class="font-bold text-2xl">댓글</h1>

	<div>
		{#each postComments as postComment}
			<div>
				<div>{postComment.id}</div>
				<div>{prettyDateTime(postComment.createDate)}</div>
				<div>{postComment.authorName}</div>
				<div>
					<img src={postComment.authorProfileImgUrl} width="30" class="rounded-full" alt="" />
				</div>
				<div>
					{#key postComment.id}
						<ToastUiEditor body={postComment.body} viewer={true} />
					{/key}
				</div>
			</div>
		{/each}
	</div>

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
