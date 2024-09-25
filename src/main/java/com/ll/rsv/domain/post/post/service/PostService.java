package com.ll.rsv.domain.post.post.service;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void write(Member author, String title, String body, boolean published) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .body(body)
                .published(published)
                .build();

        postRepository.save(post);
    }

    public List<Post> findByPublished(boolean published) {
        return postRepository.findByPublishedOrderByIdDesc(published);
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void edit(Post post, String title, String body, boolean published) {
        post.setTitle(title);
        post.setBody(body);
        post.setPublished(published);
    }

    public boolean canRead(Member actor, Post post) {
        if (actor == null) return false;
        if (post == null) return false;

        if (actor.isAdmin()) return true; // 관리자이면 가능
        if (post.isPublished()) return true; // 공개글이면 가능

        return actor.equals(post.getAuthor()); // 그것도 아니라면 본인이 쓴 글이여야 함
    }

    public boolean canEdit(Member actor, Post post) {
        if (actor == null) return false;
        if (post == null) return false;

        return actor.equals(post.getAuthor()); // 무조건 본인만 가능
    }

    public Boolean canDelete(Member actor, Post post) {
        if (actor == null) return false;
        if (post == null) return false;
        if (actor.isAdmin()) return true; // 관리자이면 가능
        return actor.equals(post.getAuthor()); // 본인이면 가능
    }
    
    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);
    }
}