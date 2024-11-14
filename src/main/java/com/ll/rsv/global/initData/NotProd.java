package com.ll.rsv.global.initData;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class NotProd {
    private final MemberService memberService;
    private final PostService postService;

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd() {
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                if (memberService.findByUsername("user1").isPresent()) return;

                Member memberUser1 = memberService.join("user1", "1234").getData();
                memberUser1.setRefreshToken("user1");

                Member memberUser2 = memberService.join("user2", "1234").getData();
                memberUser2.setRefreshToken("user2");

                Member memberUser3 = memberService.join("user3", "1234").getData();
                memberUser3.setRefreshToken("user3");

                Member memberUser4 = memberService.join("user4", "1234").getData();
                memberUser4.setRefreshToken("user4");
                //편하게 하기 위해 refreshToken 고정

                Post post1 = postService.write(memberUser1, "제목 1", "내용 1", true);

                post1.addLike(memberUser1);
                post1.addLike(memberUser2);
                post1.addLike(memberUser3);
                post1.addLike(memberUser4);

                post1.addComment(memberUser1, "댓글 1");
                post1.addComment(memberUser1, "댓글 2");
                post1.addComment(memberUser1, "댓글 3");
                post1.addComment(memberUser1, "댓글 4");
                post1.addComment(memberUser2, "댓글 5");
                post1.addComment(memberUser2, "댓글 6");
                post1.addComment(memberUser2, "댓글 7");
                post1.addComment(memberUser3, "댓글 8");
                post1.addComment(memberUser3, "댓글 9");
                post1.addComment(memberUser4, "댓글 10");

                Post post2 = postService.write(memberUser1, "제목 2", "내용 2", true);

                post2.addLike(memberUser1);
                post2.addLike(memberUser2);
                post2.addLike(memberUser3);

                post2.addComment(memberUser1, "# 댓글 11");
                post2.addComment(memberUser1, "## 댓글 12");
                post2.addComment(memberUser1, "`댓글 13`");
                post2.addComment(memberUser2, """
                        ```js
                        const title = '댓글 14';
                        ```
                        """.stripIndent().trim());
                post2.addComment(memberUser2, "댓글 15");
                post2.addComment(memberUser3, "댓글 16");

                Post post3 = postService.write(memberUser1, "제목 3", "내용 3", false);
                post3.addLike(memberUser1);
                post3.addLike(memberUser2);

                Post post4 = postService.write(memberUser1, "제목 4", "내용 4", true);
                post4.addLike(memberUser1);

                Post post5 = postService.write(memberUser2, "제목 5", "내용 5", true);
                Post post6 = postService.write(memberUser2, "제목 6", "내용 6", false);

                IntStream.rangeClosed(7, 150).forEach(i -> {
                    postService.write(memberUser3, "제목 " + i, "내용 " + i, true);
                });
            }
        };
    }
}