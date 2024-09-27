package com.ll.rsv.domain.post.post.dto;

import com.ll.rsv.domain.post.post.entity.Post;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public abstract class AbsPostDto {
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
    private String title;
    @NonNull
    private boolean published;

    @Setter
    private Boolean actorCanRead;
    @Setter
    private Boolean actorCanEdit;
    @Setter
    private Boolean actorCanDelete;

    public AbsPostDto(Post post) {
        this.id = post.getId();
        this.createDate = post.getCreateDate();
        this.modifyDate = post.getModifyDate();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getName();
        this.title = post.getTitle();
        this.published = post.isPublished();
    }
}