package com.ll.rsv.domain.post.post.controller;

import com.ll.rsv.domain.post.post.dto.PostDto;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;


    public record GetPostsResponseBody(@NonNull List<PostDto> items) {
    }

    @GetMapping("")
    public RsData<GetPostsResponseBody> getPosts() {
        List<Post> items = postService.findByPublished(true);

        return RsData.of(
                new GetPostsResponseBody(
                        items.stream()
                                .map(PostDto::new)
                                .toList()
                )
        );
    }


    // 위에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 단건 조회 시작
    // 레코드 도입하여 요청Body 클래스 단순화
    public record GetPostResponseBody(@NonNull PostDto item) {
    }

    @GetMapping("/{id}")
    public RsData<GetPostResponseBody> getPost(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        return RsData.of(
                new GetPostResponseBody(new PostDto(post))
        );
    }
    // 아래에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 단건 조회 끝


    // 위에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 글 수정 시작
    // 레코드 도입하여 요청Body 클래스 단순화
    public record EditRequestBody(@NotBlank String title, @NotBlank String body, @NotNull boolean published) {
    }

    // 레코드 도입하여 응답Body 클래스 단순화
    public record EditResponseBody(@NonNull PostDto item) {
    }

    @PutMapping(value = "/{id}")
    public RsData<EditResponseBody> edit(
            @PathVariable long id,
            @Valid @RequestBody EditRequestBody requestBody
    ) {
        Post post = postService.findById(id).orElseThrow(GlobalException.E404::new);

        postService.edit(post, requestBody.title, requestBody.body, requestBody.published);

        // public static <T> RsData<T> of(T data) { ... } 를 RsData 에 추가한 덕분에 아래와 같이 깔끔하게 변경됨
        return RsData.of(
                "%d번 글이 수정되었습니다.".formatted(id),
                new EditResponseBody(new PostDto(post))
        );
    }
    // 아래에 2줄을 띄워서 각 요청별 코드를 보기쉽게 나눠줌, 글 수정 끝
}