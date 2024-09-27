package com.ll.rsv.domain.post.postLike.entity;
import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;
@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
public class PostLike {
    @EmbeddedId
    private PostLikeId id;
    @Builder
    private static PostLike of(Post post, Member member) {
        return new PostLike(
                PostLikeId.builder()
                        .post(post)
                        .member(member)
                        .build()
        );
    }
}