package com.ll.rsv.domain.post.post.entity;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.postLike.entity.PostLike;
import com.ll.rsv.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Getter
@Setter
public class Post extends BaseTime {
    @OneToMany(mappedBy = "id.post", cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PostLike> likes = new HashSet<>();
    @Setter(PROTECTED)
    private long likesCount;
    @ManyToOne(fetch = LAZY)
    private Member author;
    private String title;
    @OneToOne(fetch = LAZY, cascade = ALL)
    @ToString.Exclude
    private PostDetail detailBody;
    private boolean published;

    public void increaseLikesCount() {
        likesCount++;
    }

    private void decreaseLikesCount() {
        likesCount--;
    }

    public void addLike(Member member) {
        boolean added = likes.add(PostLike.builder()
                .post(this)
                .member(member)
                .build());

        if (added) increaseLikesCount();
    }

    public void deleteLike(Member member) {
        boolean removed = likes.remove(
                PostLike.builder()
                        .post(this)
                        .member(member)
                        .build()
        );

        if (removed) {
            decreaseLikesCount();
        }
    }
}