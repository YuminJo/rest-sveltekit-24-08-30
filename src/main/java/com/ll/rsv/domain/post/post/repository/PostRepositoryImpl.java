package com.ll.rsv.domain.post.post.repository;
import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.standard.base.KwTypeV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import static com.ll.rsv.domain.post.post.entity.QPost.post;
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Post> findByKw(KwTypeV1 kwType, String kw, Member author, Boolean published, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (author != null) {
            builder.and(post.author.eq(author));
        }
        if (published != null) {
            builder.and(post.published.eq(published));
        }
        if (kw != null && !kw.isBlank()) {
            applyKeywordFilter(kwType, kw, builder);
        }
        JPAQuery<Post> postsQuery = createPostsQuery(builder);
        applySorting(pageable, postsQuery);
        postsQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        JPAQuery<Long> totalQuery = createTotalQuery(builder);
        return PageableExecutionUtils.getPage(postsQuery.fetch(), pageable, totalQuery::fetchOne);
    }
    private void applyKeywordFilter(KwTypeV1 kwType, String kw, BooleanBuilder builder) {
        switch (kwType) {
            case kwType.TITLE -> builder.and(post.title.containsIgnoreCase(kw));
            case kwType.BODY -> builder.and(post.detailBody.val.containsIgnoreCase(kw));
            case kwType.NAME -> builder.and(post.author.nickname.containsIgnoreCase(kw));
            default -> builder.and(
                    post.title.containsIgnoreCase(kw)
                            .or(post.detailBody.val.containsIgnoreCase(kw))
                            .or(post.author.nickname.containsIgnoreCase(kw))
            );
        }
    }
    private JPAQuery<Post> createPostsQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .where(builder);
    }
    private void applySorting(Pageable pageable, JPAQuery<Post> postsQuery) {
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(post.getType(), post.getMetadata());
            postsQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }
    }
    private JPAQuery<Long> createTotalQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(builder);
    }
}