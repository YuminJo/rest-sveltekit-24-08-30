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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostLike {
    // 어짜피 좋아요 라는 것은 Post 와 Member 의 조합이 유니크 하기 때문에, 따로 Long id 을 잡지 않고 복합키로 설정
    @EmbeddedId // 복합키를 사용하기에 이렇게 한다.
    @Delegate // 이걸 사용하면 안에 있는 메서드를 밖으로 빼서 public 사용
    @EqualsAndHashCode.Include
    private PostLikeId id;

    // 이 메서드를 만들어 줘야 외부에서는 PostLike 를 빌더로 만들 때 .post(), 와 .member() 를 이용할 수 있다.
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