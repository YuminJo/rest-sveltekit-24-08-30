package com.ll.rsv.domain.post.postComment.dto;

import com.ll.rsv.domain.post.postComment.entity.PostComment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class PostCommentDto {
    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createDate;
    @NonNull
    private LocalDateTime modifyDate;
    @NonNull
    private long authorId;
    @NonNull
    private String authorName;
    @NonNull
    private String authorProfileImgUrl;
    @NonNull
    private String body;

    @Setter
    private Boolean actorCanEdit;
    @Setter
    private Boolean actorCanDelete;

    // 추가적인
    private boolean editing;

    public PostCommentDto(PostComment postComment) {
        this.id = postComment.getId();
        this.createDate = postComment.getCreateDate();
        this.modifyDate = postComment.getModifyDate();
        this.authorId = postComment.getAuthor().getId();
        this.authorName = postComment.getAuthor().getName();
        this.authorProfileImgUrl = postComment.getAuthor().getProfileImgUrlOrDefault();
        this.body = postComment.getBody();
    }
}