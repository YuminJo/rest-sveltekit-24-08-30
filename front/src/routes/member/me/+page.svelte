<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import ut from '$lib/util/ut';
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
		<div>{ut.prettyDate(rq.member.createDate)}</div>
	</div>
	<div>
		<div>이름</div>
		<div>{rq.member.name}</div>
	</div>
</div>
