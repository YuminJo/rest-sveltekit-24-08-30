package com.ll.rsv.domain.post.post.repository;


import com.ll.rsv.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByPublishedOrderByIdDesc(boolean published);
    
}
