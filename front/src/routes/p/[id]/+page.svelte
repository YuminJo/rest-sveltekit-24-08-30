<script lang="ts">
	import { page } from '$app/stores';
	import rq from '$lib/rq/rq.svelte';
	import hotkeys from 'hotkeys-js';

	import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';

	let toastUiEditor: any | undefined;
	let oldBody: string = '';

	function saveToLocalStorage(id: number, body: string) {
		const key = 'posts_' + id;
		// LocalStorage에서 기존 데이터를 가져옵니다.
		const existingData = localStorage.getItem(key);

		// 기존 데이터가 있으면 JSON으로 파싱하고, 없으면 빈 배열을 사용합니다.
		const posts = existingData ? JSON.parse(existingData) : [];

		// 새 데이터를 배열에 추가합니다.
		posts.push({
			id,
			body: body,
			date: new Date().toISOString()
		});

		// 배열의 크기가 10을 초과하면 가장 오래된 항목(첫 번째 항목)을 제거합니다.
		if (posts.length > 10) {
			posts.shift(); // 배열의 첫 번째 항목을 제거합니다.
		}

		// 변경된 배열을 JSON 문자열로 변환하여 LocalStorage에 저장합니다.
		localStorage.setItem(key, JSON.stringify(posts));
	}

	async function Post__saveBody() {
		const newBody = toastUiEditor.editor.getMarkdown().trim();

		if (oldBody === newBody) {
			return;
		}

		const { data, error } = await rq.apiEndPoints().PUT('/api/v1/posts/{id}/body', {
			params: { path: { id: parseInt($page.params.id) } },
			body: { body: newBody }
		});

		oldBody = newBody;

		saveToLocalStorage(parseInt($page.params.id), newBody);

		if (data) {
			rq.msgInfo(data.msg);
		}
	}

	rq.effect(() => {
		hotkeys.filter = function (event) {
			return true;
		};

		hotkeys('ctrl+s,cmd+s', 'postEdit', function (event, handler) {
			Post__saveBody();

			event.preventDefault();
		});

		hotkeys('ctrl+q,cmd+q', 'postEdit', function (event, handler) {
			toastUiEditor.switchTab();
			event.preventDefault();
		});

		hotkeys.setScope('postEdit');

		return () => {
			hotkeys.deleteScope('postEdit');
		};
	});

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
				<ToastUiEditor bind:this={toastUiEditor} body={post.body} saveBody={Post__saveBody} />
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
