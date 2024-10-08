package com.ll.rsv.domain.post.post.controller;

import com.ll.rsv.domain.post.post.dto.PostDto;
import com.ll.rsv.domain.post.post.dto.PostWithBodyDto;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;


    public record MakeTempResponseBody(@NonNull PostDto item) {
    }

    @Transactional
    @PostMapping("/temp")
    public RsData<MakeTempResponseBody> makeTemp() {
        RsData<Post> findTempOrMakeRsData = postService.findTempOrMake(rq.getMember());

        return findTempOrMakeRsData.newDataOf(
                new MakeTempResponseBody(
                        postToDto(findTempOrMakeRsData.getData())
                )
        );
    }


    public record GetPostsResponseBody(@NonNull List<PostDto> items) {
    }

    @GetMapping("")
    public RsData<GetPostsResponseBody> getPosts() {
        List<Post> items = postService.findByPublished(true);

        if (rq.isLogin()) {
            // 로그인 했다면 먼저 해당 회원이 각 글들에 대해서 추천을 했는지를 true, false 형태로 정리한 맵을 먼저 만들고 캐시에 등록한다.
            // 이 작업을 안해도 아래기능이 실행되기는 하지만 각 글들을의 모든 추천정보를 전부다 가져오는 쿼리가 실행되어 버린다.
            postService.loadLikeMap(items, rq.getMember());
        }

        List<PostDto> _items = items.stream()
                .map(this::postToDto)// 이 작업에서 캐시가 없었다면, 추가 쿼리가 발생한다.
                .collect(Collectors.toList());

        return RsData.of(
                new GetPostsResponseBody(
                        _items
                )
        );
    }

    
    // 위에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 단건 조회 시작
    // 레코드 도입하여 요청Body 클래스 단순화
    public record GetPostResponseBody(@NonNull PostWithBodyDto item) {
    }

    @GetMapping("/{id}")
    public RsData<GetPostResponseBody> getPost(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canRead(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        PostWithBodyDto dto = postToWithBodyDto(post);

        return RsData.of(
                new GetPostResponseBody(dto)
        );
    }
    // 아래에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 단건 조회 끝


    // 위에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 글 수정 시작
    // 레코드 도입하여 요청Body 클래스 단순화
    public record EditRequestBody(@NotBlank String title, @NotBlank String body, @NotNull boolean published) {
    }
    
    // 레코드 도입하여 응답Body 클래스 단순화
    public record EditResponseBody(@NonNull PostWithBodyDto item) {
    }

    @PutMapping(value = "/{id}")
    @Transactional
    public RsData<EditResponseBody> edit(
            @PathVariable long id,
            @Valid @RequestBody EditRequestBody requestBody
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canEdit(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.edit(post, requestBody.title, requestBody.body, requestBody.published);

        // public static <T> RsData<T> of(T data) { ... } 를 RsData 에 추가한 덕분에 아래와 같이 깔끔하게 변경됨
        return RsData.of(
                "%d번 글이 수정되었습니다.".formatted(id),
                new EditResponseBody(new PostWithBodyDto(post))
        );
    }


    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Empty> delete(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canDelete(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.delete(post);

        return RsData.of(
                "%d번 글이 삭제되었습니다.".formatted(id)
        );
    }


    public record LikeResponseBody(@NonNull PostDto item) {
    }

    @PostMapping(value = "/{id}/like")
    @Transactional
    public RsData<LikeResponseBody> like(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canLike(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.like(rq.getMember(), post);

        PostDto dto = postToDto(post);

        return RsData.of(
                "%d번 글을 추천하였습니다.".formatted(id),
                new LikeResponseBody(dto)
        );
    }


    public record CancelLikeResponseBody(@NonNull PostDto item) {
    }

    @DeleteMapping(value = "/{id}/cancelLike")
    @Transactional
    public RsData<CancelLikeResponseBody> cancelLike(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canCancelLike(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.cancelLike(rq.getMember(), post);

        PostDto dto = postToDto(post);

        return RsData.of(
                "%d번 글을 추천취소하였습니다.".formatted(id),
                new CancelLikeResponseBody(dto)
        );
    }

    private PostDto postToDto(Post post) {
        PostDto dto = new PostDto(post);

        loadAdditionalInfo(dto, post);

        return dto;
    }

    private PostWithBodyDto postToWithBodyDto(Post post) {
        PostWithBodyDto dto = new PostWithBodyDto(post);

        loadAdditionalInfo(dto, post);

        return dto;
    }

    private void loadAdditionalInfo(PostDto dto, Post post) {
        dto.setActorCanRead(postService.canRead(rq.getMember(), post));
        dto.setActorCanEdit(postService.canEdit(rq.getMember(), post));
        dto.setActorCanDelete(postService.canDelete(rq.getMember(), post));
        dto.setActorCanLike(postService.canLike(rq.getMember(), post));
        dto.setActorCanCancelLike(postService.canCancelLike(rq.getMember(), post));
    }
}