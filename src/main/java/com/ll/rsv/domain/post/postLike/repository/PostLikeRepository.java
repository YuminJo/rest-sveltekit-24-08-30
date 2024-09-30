package com.ll.rsv.domain.post.postLike.repository;
import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.postLike.entity.PostLike;
import com.ll.rsv.domain.post.postLike.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    List<PostLike> findByIdPostInAndIdMember(List<Post> posts, Member member);
}