package com.ll.rsv.domain.post.post.service;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.entity.PostDetail;
import com.ll.rsv.domain.post.post.repository.PostDetailRepository;
import com.ll.rsv.domain.post.post.repository.PostRepository;
import com.ll.rsv.domain.post.postLike.entity.PostLike;
import com.ll.rsv.domain.post.postLike.repository.PostLikeRepository;
import com.ll.rsv.global.transactionCache.TransactionCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostDetailRepository postDetailRepository;
    private final PostLikeRepository postLikeRepository;
    private final TransactionCache transactionCache;

    @Transactional
    public Post write(Member author, String title, String body, boolean published) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .published(published)
                .build();

        postRepository.save(post);

        post.setDetailBody(
                PostDetail
                        .builder()
                        .post(post)
                        .name("common__body")
                        .build()
        );

        saveBody(post, body);

        return post;
    }

    private void saveBody(Post post, String body) {
        PostDetail detailBody = post.getDetailBody();

        if (detailBody == null) {
            detailBody = findDetailOrMake(post, "common__body");
            post.setDetailBody(detailBody);
        }

        detailBody.setVal(body);
    }

    private PostDetail findDetailOrMake(Post post, String name) {
        Optional<PostDetail> opDetailBody = postDetailRepository.findByPostAndName(post, name);

        PostDetail detailBody = opDetailBody.orElseGet(() -> postDetailRepository.save(
                PostDetail.builder()
                        .post(post)
                        .name(name)
                        .build()
        ));

        return detailBody;
    }

    public List<Post> findByPublished(boolean published) {
        return postRepository.findByPublishedOrderByIdDesc(published);
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public void edit(Post post, String title, String body, boolean published) {
        post.setTitle(title);
        post.setPublished(published);

        saveBody(post, body);
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

    public Boolean canLike(Member actor, Post post) {
        if (actor == null) return false;
        if (post == null) return false;

        Map<Long, Boolean> likeMap = transactionCache.get("likeMap");
        if (likeMap != null && likeMap.containsKey(post.getId())) return !likeMap.get(post.getId());

        return !post.hasLike(actor);
    }

    public Boolean canCancelLike(Member actor, Post post) {
        if (actor == null) return false;
        if (post == null) return false;

        Map<Long, Boolean> likeMap = transactionCache.get("likeMap");
        if (likeMap != null && likeMap.containsKey(post.getId())) return likeMap.get(post.getId());

        return post.hasLike(actor);
    }

    @Transactional
    public void like(Member actor, Post post) {
        post.addLike(actor);
    }

    @Transactional
    public void cancelLike(Member actor, Post post) {
        post.deleteLike(actor);
    }

    // 트랜잭션 스코프 빈으로 관리되는 transactionCache 에 생성한 캐시데이터를 등록한다.
    // 해당 캐시는 해당 트랜잭션이 끝나면 사라진다.
    public void loadLikeMap(List<Post> posts, Member member) {
        List<PostLike> likes = findLikesByPostInAndMember(posts, member);

        Map<Long, Boolean> likeMap_ = likes
                .stream()
                .collect(
                        HashMap::new,
                        (map, like) -> map.put(like.getPost().getId(), true),
                        HashMap::putAll
                );

        Map<Long, Boolean> likeMap = posts
                .stream()
                .collect(
                        HashMap::new,
                        (map, post) -> map.put(post.getId(), likeMap_.getOrDefault(post.getId(), false)),
                        HashMap::putAll
                );

        transactionCache.put("likeMap", likeMap);
    }

    private List<PostLike> findLikesByPostInAndMember(List<Post> posts, Member member) {
        return postLikeRepository.findByIdPostInAndIdMember(posts, member);
    }
}