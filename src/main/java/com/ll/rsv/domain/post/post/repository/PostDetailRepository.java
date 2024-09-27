package com.ll.rsv.domain.post.post.repository;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.entity.PostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {
    Optional<PostDetail> findByPostAndName(Post post, String name);
}