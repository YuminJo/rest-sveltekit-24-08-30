package com.ll.rsv.domain.post.post.controller;

import com.ll.rsv.domain.post.post.dto.PostDto;
import com.ll.rsv.domain.post.post.dto.PostWithBodyDto;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import com.ll.rsv.global.app.AppConfig;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import com.ll.rsv.standard.base.KwTypeV1;
import com.ll.rsv.standard.base.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/api/v1/posts", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Tag(name = "ApiV1PostController", description = "글 CRUD 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;


    public record MakeTempResponseBody(@NonNull PostDto item) {
    }

    @Transactional
    @PostMapping(value = "/temp", consumes = ALL_VALUE)
    @Operation(summary = "임시 글 생성")
    public RsData<MakeTempResponseBody> makeTemp() {
        RsData<Post> findTempOrMakeRsData = postService.findTempOrMake(rq.getMember());

        return findTempOrMakeRsData.newDataOf(
                new MakeTempResponseBody(
                        postToDto(findTempOrMakeRsData.getData())
                )
        );
    }


    public record GetPostsResponseBody(@NonNull PageDto<PostDto> itemPage) {
    }

    @GetMapping(value = "", consumes = ALL_VALUE)
    @Operation(summary = "글 다건조회")
    public RsData<GetPostsResponseBody> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "ALL") KwTypeV1 kwType
    ) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
        Page<Post> itemPage = postService.findByKw(kwType, kw, null, true, pageable);

        if (rq.isLogin()) {
            postService.loadLikeMap(itemPage.getContent(), rq.getMember());
        }

        Page<PostDto> _itemPage = itemPage.map(this::postToDto);

        return RsData.of(
                new GetPostsResponseBody(
                        new PageDto<>(_itemPage)
                )
        );
    }


    public record GetPostResponseBody(@NonNull PostWithBodyDto item) {
    }

    @GetMapping(value = "/{id}", consumes = ALL_VALUE)
    @Operation(summary = "글 단건조회")
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


    public record EditRequestBody(@NotBlank String title, @NotBlank String body, @NotNull boolean published) {
    }

    public record EditResponseBody(@NonNull PostWithBodyDto item) {
    }

    @PutMapping("/{id}")
    @Operation(summary = "글 편집")
    @Transactional
    public RsData<EditResponseBody> edit(
            @PathVariable long id,
            @Valid @RequestBody EditRequestBody requestBody
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canEdit(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.edit(post, requestBody.title, requestBody.body, requestBody.published);

        return RsData.of(
                "%d번 글이 수정되었습니다.".formatted(id),
                new EditResponseBody(postToWithBodyDto(post))
        );
    }


    public record EditBodyRequestBody(@NotBlank String body) {
    }


    @PutMapping("/{id}/body")
    @Operation(summary = "글 본문 편집")
    @Transactional
    public RsData<Empty> editBody(
            @PathVariable long id,
            @Valid @RequestBody EditBodyRequestBody requestBody
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canEdit(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postService.editBody(post, requestBody.body);

        return RsData.of(
                "저장"
        );
    }


    public record GetPostBodyResponseBody(
            @NotNull LocalDateTime modifyDate,
            @NotNull String body
    ) {
    }

    @GetMapping(value = "/{id}/body", consumes = ALL_VALUE)
    @Operation(summary = "글(본문) 단건조회")
    @Transactional
    public RsData<GetPostBodyResponseBody> getPostBody(
            @PathVariable long id,
            @RequestParam LocalDateTime lastModifyDate
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        if (!postService.canRead(rq.getMember(), post))
            throw new GlobalException("403-1", "권한이 없습니다.");

        if (post.getModifyDate().isBefore(lastModifyDate) || post.getModifyDate().equals(lastModifyDate))
            throw new GlobalException("400-1", "변경사항이 없습니다.");

        return RsData.of(
                "새 본문을 불러옵니다.",
                new GetPostBodyResponseBody(
                        post.getModifyDate(),
                        post.getDetailBody().getVal()
                )
        );
    }


    @DeleteMapping(value = "/{id}", consumes = ALL_VALUE)
    @Operation(summary = "글 삭제")
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

    @PostMapping(value = "/{id}/like", consumes = ALL_VALUE)
    @Transactional
    @Operation(summary = "글 추천")
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

    @DeleteMapping(value = "/{id}/cancelLike", consumes = ALL_VALUE)
    @Operation(summary = "글 추천취소")
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