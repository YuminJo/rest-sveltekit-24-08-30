package com.ll.rsv.domain.post.post.repository;
import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.standard.base.KwTypeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PostRepositoryCustom {
    Page<Post> findByKw(KwTypeV1 kwType, String kw, Member author, Boolean published, Pageable pageable);
}