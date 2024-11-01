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
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/postComments")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1PostCommentController {
    private final Rq rq;
    private final PostService postService;
    private final PostCommentService postCommentService;


    public record GetPostCommentsResponseBody(
            @NonNull List<PostCommentDto> items
    ) {
    }

    @GetMapping("/{postId}")
    public RsData<GetPostCommentsResponseBody> getPosts(
            @PathVariable long postId
    ) {
        List<PostComment> items = postService.findById(postId)
                .orElseThrow(GlobalException.E404::new)
                .getComments();

        List<PostCommentDto> _items = items.stream()
                .map(this::postCommentToDto)
                .collect(Collectors.toList());

        return RsData.of(
                new GetPostCommentsResponseBody(
                        _items
                )
        );
    }


    @DeleteMapping("/{postId}/{postCommentId}")
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

        postService.deleteComment(post, postComment);

        return RsData.of(
                "댓글이 삭제되었습니다."
        );
    }


    private PostCommentDto postCommentToDto(PostComment postComment) {
        PostCommentDto dto = new PostCommentDto(postComment);
        dto.setActorCanDelete(postCommentService.canDelete(rq.getMember(), postComment));
        dto.setActorCanEdit(postCommentService.canEdit(rq.getMember(), postComment));

        return dto;
    }
}