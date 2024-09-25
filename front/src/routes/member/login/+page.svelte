<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function submitLoginForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		form.username.value = form.username.value.trim();

		if (form.username.value.length === 0) {
			rq.msgError('아이디를 입력해주세요.');
			form.username.focus();

			return;
		}

		form.password.value = form.password.value.trim();

		if (form.password.value.length === 0) {
			rq.msgError('비밀번호를 입력해주세요.');
			form.password.focus();

			return;
		}

		const { data, error } = await rq.apiEndPoints().POST('/api/v1/members/login', {
			body: {
				username: form.username.value,
				password: form.password.value
			}
		});

		if (error)
			rq.msgError(error.msg); // 로그인에 실패했다면 메세지만 출력
		else {
			// 로그인에 성공했다면 메세지 출력 후 이동
			// 이동 후에는 rq.member 객체에 값을 채워서 로그인 처리를 수행
			rq.msgAndRedirect(data, undefined, '/', () => rq.setLogined(data.data.item));
		}
	}
</script>

<form on:submit|preventDefault={submitLoginForm}>
	<div>
		<div>아이디</div>
		<input type="text" name="username" placeholder="아이디" />
	</div>

	<div>
		<div>비밀번호</div>
		<input type="password" name="password" placeholder="비밀번호" />
	</div>

	<div>
		<div>로그인</div>
		<button type="submit">로그인</button>
		<a href="?">카카오로그인</a>
	</div>
</form>
