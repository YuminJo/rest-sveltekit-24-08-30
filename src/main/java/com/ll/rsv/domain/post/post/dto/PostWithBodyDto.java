package com.ll.rsv.domain.post.post.dto;

import com.ll.rsv.domain.post.post.entity.Post;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class PostWithBodyDto extends AbsPostDto {
    @NonNull
    private String body;

    public PostWithBodyDto(Post post) {
        super(post);
        this.body = post.getDetailBody().getVal();
    }
}