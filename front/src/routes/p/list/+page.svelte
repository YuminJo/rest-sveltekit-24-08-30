<script lang="ts">
	import { page } from '$app/stores';

	import rq from '$lib/rq/rq.svelte';
	import { prettyDateTime } from '$lib/utils';
	import type { components } from '$lib/types/api/v1/schema';
	import Pagination from '$lib/components/Pagination.svelte';
	import type { KwTypeV1 } from '$lib/types';

	let posts: components['schemas']['PostDto'][] = $state([]);

	async function load() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const kw = $page.url.searchParams.get('kw') ?? '';
		const kwType = ($page.url.searchParams.get('kwType') ?? 'ALL') as KwTypeV1;
		const page_ = parseInt($page.url.searchParams.get('page') ?? '1');

		const { data } = await rq.apiEndPoints().GET('/api/v1/posts', {
			params: {
				query: {
					kw,
					kwType,
					page: page_
				}
			}
		});

		posts = data!.data.itemPage.content;

		return data!;
	}
</script>

{#await load()}
	<p>loading...</p>
{:then { data: { itemPage } }}
	<h1>검색</h1>

	<form action="/p/list" class="bg-base rounded flex flex-col gap-6">
		<div class="form-control">
			<!-- svelte-ignore a11y-label-has-associated-control -->
			<label class="label">
				<span class="label-text">검색필터</span>
			</label>

			<select
				name="kwType"
				class="select select-bordered"
				value={$page.url.searchParams.get('kwType') ?? 'ALL'}
			>
				<option value="ALL">전체</option>
				<option value="TITLE">제목</option>
				<option value="BODY">내용</option>
				<option value="NAME">작성자</option>
			</select>
		</div>

		<div class="form-control">
			<!-- svelte-ignore a11y-label-has-associated-control -->
			<label class="label">
				<span class="label-text">검색어</span>
			</label>

			<input
				placeholder="검색어"
				class="input input-bordered"
				name="kw"
				type="search"
				value={$page.url.searchParams.get('kw') ?? ''}
				autocomplete="off"
			/>
		</div>

		<div>
			<button class="btn btn-block btn-primary gap-1">
				<i class="fa-solid fa-magnifying-glass"></i>
				<span>검색</span>
			</button>
		</div>
	</form>

	<h1>글 리스트</h1>

	<div>
		<div>전체 페이지 아이템 : {itemPage.totalElementsCount}</div>
		<div>현재 페이지 아이템 : {itemPage.pageElementsCount}</div>
	</div>

	<Pagination page={itemPage} />

	<ul class="grid grid-cols-1 gap-3">
		{#each posts as post}
			<li>
				<div class="flex items-center gap-2">
					<a href="/p/{post.id}">{post.id}. {post.title}</a>
					<span>추천 : {post.likesCount}</span>
					<span>작성일 : {prettyDateTime(post.createDate)}</span>

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

	<Pagination page={itemPage} />
{/await}
