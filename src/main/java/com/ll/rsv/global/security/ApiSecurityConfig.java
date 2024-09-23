package com.ll.rsv.global.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig {
    @Bean
    @Order(2) // SecurityConfig::filterChain 보다 늦게 실행되게 하려고 추가
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests
                                // 글 단건조회, 조회는 인증이 필요 없음
                                .requestMatchers(HttpMethod.GET, "/api/*/posts/{id:\\d+}", "/api/*/posts")
                                .permitAll()
                                // 로그인, 로그아웃도 인증이 필요 없음
                                .requestMatchers("/api/*/members/login", "/api/*/members/logout")
                                .permitAll()
                                .anyRequest()
                                .permitAll() // 일단 여기를 이런식으로 허용시키고, 나중에 인증필요(.authenticated()) 로 교체해야 한다.
                )
                .csrf(
                        csrf -> csrf
                                .disable()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                );

        return http.build();
    }
}