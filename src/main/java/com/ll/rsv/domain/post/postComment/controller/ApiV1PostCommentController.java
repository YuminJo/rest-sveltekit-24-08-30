package com.ll.rsv.domain.post.postComment.controller;

import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import com.ll.rsv.domain.post.postComment.dto.PostCommentDto;
import com.ll.rsv.domain.post.postComment.entity.PostComment;
import com.ll.rsv.domain.post.postComment.service.PostCommentService;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/postComments", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Tag(name = "ApiV1PostCommentController", description = "댓글 CRUD 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1PostCommentController {
    private final Rq rq;
    private final PostService postService;
    private final PostCommentService postCommentService;
    @PersistenceContext
    private EntityManager entityManager;


    public record GetPostCommentsResponseBody(
            @NonNull List<PostCommentDto> items
    ) {
    }

    @GetMapping(value = "/{postId}", consumes = ALL_VALUE)
    @Operation(summary = "댓글 다건조회")
    public RsData<GetPostCommentsResponseBody> getPosts(
            @PathVariable long postId
    ) {
        List<PostComment> items = postService.findById(postId)
                .orElseThrow(GlobalException.E404::new)
                .getComments()
                .stream()
                .filter(PostComment::isPublished)
                .toList();

        List<PostCommentDto> _items = items.stream()
                .map(this::postCommentToDto)
                .collect(Collectors.toList());

        return RsData.of(
                new GetPostCommentsResponseBody(
                        _items
                )
        );
    }


    @DeleteMapping(value = "/{postId}/{postCommentId}", consumes = ALL_VALUE)
    @Operation(summary = "댓글 삭제")
    @Transactional
    public RsData<Empty> delete(
            @PathVariable long postId,
            @PathVariable long postCommentId
    ) {
        Post post = postService.findById(postId).orElseThrow(GlobalException.E404::new);

        PostComment postComment = post.findCommentById(postCommentId)
                .orElseThrow(GlobalException.E404::new);

        if (!postCommentService.canDelete(rq.getMember(), postComment))
            throw new GlobalException("403-1", "권한이 없습니다.");

        postCommentService.delete(post, postComment);

        return RsData.of(
                "댓글이 삭제되었습니다."
        );
    }


    public record WriteCommentResponseBody(@NonNull PostCommentDto item) {
    }

    @PostMapping(value = "/{postId}/temp", consumes = ALL_VALUE)
    @Operation(summary = "임시 댓글 생성")
    @Transactional
    public RsData<WriteCommentResponseBody> makeTemp(
            @PathVariable long postId
    ) {
        Post post = postService.findById(postId).orElseThrow(GlobalException.E404::new);

        RsData<PostComment> findTempOrMakeRsData = postCommentService.findTempOrMake(rq.getMember(), post);

        entityManager.flush();

        return findTempOrMakeRsData.newDataOf(
                new WriteCommentResponseBody(postCommentToDto(findTempOrMakeRsData.getData()))
        );
    }


    public record EditCommentRequestBody(@NotBlank String body) {
    }

    public record EditCommentResponseBody(@NonNull PostCommentDto item) {
    }

    @PutMapping("/{postId}/{postCommentId}")
    @Operation(summary = "댓글 수정")
    @Transactional
    public RsData<EditCommentResponseBody> edit(
            @PathVariable long postId,
            @PathVariable long postCommentId,
            @Valid @RequestBody ApiV1PostCommentController.EditCommentRequestBody body
    ) {
        Post post = postService.findById(postId).orElseThrow(GlobalException.E404::new);

        PostComment postComment = post.findCommentById(postCommentId)
                .orElseThrow(GlobalException.E404::new);

        postCommentService.edit(post, postComment, body.body);

        return RsData.of(
                "댓글이 수정되었습니다.",
                new EditCommentResponseBody(postCommentToDto(postComment))
        );
    }


    private PostCommentDto postCommentToDto(PostComment postComment) {
        PostCommentDto dto = new PostCommentDto(postComment);
        dto.setActorCanDelete(postCommentService.canDelete(rq.getMember(), postComment));
        dto.setActorCanEdit(postCommentService.canEdit(rq.getMember(), postComment));

        return dto;
    }
}