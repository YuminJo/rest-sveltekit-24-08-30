<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';
	import { prettyDateTime } from '$lib/utils';
	import type { components } from '$lib/types/api/v1/schema';

	async function loadPost() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const { data, error } = await rq
			.apiEndPoints()
			.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });

		if (error) throw error;

		return data!;
	}

	async function loadPostComments() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const { data, error } = await rq.apiEndPoints().GET('/api/v1/postComments/{postId}', {
			params: { path: { postId: parseInt($page.params.id) } }
		});

		if (error) throw error;

		return data!;
	}

	async function confirmAndDeletePostComment(
		postComment: components['schemas']['PostCommentDto'],
		callback: (data: components['schemas']['RsDataEmpty']) => void
	) {
		const { data, error } = await rq
			.apiEndPoints()
			.DELETE('/api/v1/postComments/{postId}/{postCommentId}', {
				params: {
					path: { postId: parseInt($page.params.id), postCommentId: postComment.id }
				}
			});

		if (error) throw error;

		callback(data!);
	}
</script>

{#await loadPost()}
	<div>loading...</div>
{:then { data: { item: post } }}
	<h1>번호 : {post.id}</h1>
	<h1>제목 : {post.title}</h1>
	<div>작성 : {prettyDateTime(post.createDate)}</div>
	<div>수정 : {prettyDateTime(post.modifyDate)}</div>
	<div>작성자 : {post.authorName}</div>
	<div>
		<img src={post.authorProfileImgUrl} width="30" class="rounded-full" alt="" />
	</div>
	<div>추천 : {post.likesCount}</div>

	{#key post.id}
		<ToastUiEditor body={post.body} viewer={true} />
	{/key}

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

{#await loadPostComments()}
	<div>loading...</div>
{:then { data: { items: postComments } }}
	<h1 class="font-bold text-2xl">댓글</h1>

	<div>
		{#each postComments as postComment}
			<div>
				<div>번호 : {postComment.id}</div>
				<div>작성 : {prettyDateTime(postComment.createDate)}</div>
				<div>수정 : {prettyDateTime(postComment.modifyDate)}</div>
				<div>작성자 : {postComment.authorName}</div>
				<div>
					<img src={postComment.authorProfileImgUrl} width="30" class="rounded-full" alt="" />
				</div>
				<div>
					{#key postComment.id}
						<ToastUiEditor body={postComment.body} viewer={true} />
					{/key}
				</div>

				<div>
					{#if postComment.actorCanDelete}
						<button
							onclick={() =>
								confirmAndDeletePostComment(postComment, (data) => {
									rq.msgInfo(data.msg);
								})}>삭제</button
						>
					{/if}

					{#if postComment.actorCanEdit}{/if}
				</div>
			</div>
		{/each}
	</div>
{/await}
