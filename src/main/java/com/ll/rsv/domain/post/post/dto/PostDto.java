package com.ll.rsv.domain.post.post.dto;

import com.ll.rsv.domain.post.post.entity.Post;
import lombok.Getter;

@Getter
public class PostDto extends AbsPostDto {
    public PostDto(Post post) {
        super(post);
    }
}