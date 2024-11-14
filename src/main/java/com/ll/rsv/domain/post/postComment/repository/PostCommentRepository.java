package com.ll.rsv.domain.post.postComment.repository;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.postComment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Optional<PostComment> findTop1ByPostAndAuthorAndPublishedAndBodyOrderByIdDesc(Post post, Member author, boolean published, String body);
}