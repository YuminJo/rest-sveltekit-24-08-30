<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { prettyDate } from '$lib/utils';
</script>

<h1>내 정보</h1>
<div>
	<div>
		<div>
			<img src={rq.member.profileImgUrl} width="100" class="rounded" alt="" />
		</div>
	</div>
	<div>
		<div>번호</div>
		<div>{rq.member.id}</div>
	</div>
	<div>
		<div>가입날짜</div>
		<div>{prettyDate(rq.member.createDate)}</div>
	</div>
	<div>
		<div>이름</div>
		<div>{rq.member.name}</div>
	</div>
</div>
