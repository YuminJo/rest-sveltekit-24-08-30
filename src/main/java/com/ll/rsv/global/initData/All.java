package com.ll.rsv.global.initData;

import com.ll.rsv.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

// 테스트모드 운영모드 개발모드 전부 실행
// 어떤 시스템이건 어드민과 시스템 계정은 있어야 하므로 이를 생성하는 코드

@Configuration
@Slf4j
@RequiredArgsConstructor
public class All {
    private final MemberService memberService;

    @Bean
    @Order(2)
    public ApplicationRunner initAll() {
        return args -> {
            if (memberService.findByUsername("system").isPresent()) return;

            memberService.join("system", "1234");
            memberService.join("admin", "1234");
        };
    }
}
