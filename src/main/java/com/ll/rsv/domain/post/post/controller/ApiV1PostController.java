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
    public final Rq rq;
    private final PostService postService;


    public record GetPostsResponseBody(@NonNull List<PostDto> items) {
    }

    @GetMapping("")
    public RsData<GetPostsResponseBody> getPosts() {
        List<Post> items = postService.findByPublished(true);
        List<PostDto> _items = items.stream()
                .map(this::postToDto)
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
    // 아래에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 글 수정 끝
    
    @DeleteMapping(value = "/{id}")
    @Transactional
    public RsData<Empty> delete(@PathVariable long id)
    {
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