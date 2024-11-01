<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';

	import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';

	let toastUiEditor: any | undefined;

	async function load() {
		if (import.meta.env.SSR) throw new Error('CSR ONLY');

		const { data, error } = await rq
			.apiEndPoints()
			.GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });

		if (error) throw error;

		return data!;
	}

	async function submitLoginForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		const titleInput = form.elements.namedItem('title') as HTMLInputElement;

		if (titleInput.value.length === 0) {
			rq.msgError('제목을 입력해주세요.');
			titleInput.focus();
			return;
		}

		toastUiEditor.editor.setMarkdown(toastUiEditor.editor.getMarkdown().trim());

		if (toastUiEditor.editor.getMarkdown().trim().length === 0) {
			rq.msgError('내용을 입력해주세요.');
			toastUiEditor.editor.focus();
			return;
		}

		const { data, error } = await rq.apiEndPoints().PUT('/api/v1/posts/{id}', {
			params: { path: { id: parseInt($page.params.id) } },
			body: {
				title: titleInput.value,
				body: toastUiEditor.editor.getMarkdown().trim(),
				published: form.published.checked
			}
		});

		rq.msgAndRedirect(data, error, '/p/' + $page.params.id);
	}
</script>

{#await load()}
	<div>loading...</div>
{:then { data: { item: post } }}
	<form on:submit|preventDefault={submitLoginForm}>
		<div>
			<div>번호</div>
			<div>번호 : {post.id}</div>
		</div>

		<div>
			<div>추천</div>
			<div>추천 : {post.likesCount}</div>
		</div>

		<div>
			<div>공개</div>
			<input type="checkbox" name="published" value={true} checked={post.published} />
		</div>

		<div>
			<div>제목</div>
			<input type="text" name="title" value={post.title} />
		</div>

		<div>
			<div>내용</div>
			{#key post.id}
				<ToastUiEditor bind:this={toastUiEditor} body={post.body} />
			{/key}
		</div>

		<div>
			<div>저장</div>
			<button type="submit">저장</button>
		</div>
	</form>
{:catch error}
	{error.msg}
{/await}
