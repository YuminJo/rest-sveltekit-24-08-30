<!-- 이 페이지에 머무는 일은 없다. reload()를 구현하기 위해, 이 페이지에 왔다가 바로 다시 `?url=주소` 에 적인 주소로 다시 돌아간다. 사용자 입장에서는 reload 한것 처럼 보인다. -->
<script lang="ts">
	import { onMount } from 'svelte';
	import rq from '$lib/rq/rq.svelte';
	onMount(() => {
		const params = new URLSearchParams(window.location.search);
		const url = params.get('url');
		if (url) rq.replace(url);
	});
</script>
