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

	async function submitLoginForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		const titleInput = form.elements.namedItem('title') as HTMLInputElement;

		if (titleInput.value.length === 0) {
			rq.msgError('Username is required');
			titleInput.focus();
			return;
		}

		const title = titleInput.value.trim();

		form.body.value = form.body.value.trim();
		if (form.body.value.length === 0) {
			rq.msgError('Password is required');
			form.body.focus();
			return;
		}

		const body = form.body.value;

		const { data, error } = await rq.apiEndPoints().PUT('/api/v1/posts/{id}', {
			params: { path: { id: parseInt($page.params.id) } },
			body: { title, body }
		});
	}
</script>

{#await load()}
	<div>loading...</div>
{:then { data: { item: post } }}
	<form action="" on:submit|preventDefault={submitLoginForm}>
		<div>
			<div>제목</div>
			<input type="text" name="title" value={post.title} />
		</div>
		<div>
			<div>내용</div>
			<textarea name="body">{post.body}</textarea>
		</div>
		<div>
			<div>저장</div>
			<button type="submit">저장</button>
		</div>
	</form>
{:catch error}
	{error.msg}
{/await}
