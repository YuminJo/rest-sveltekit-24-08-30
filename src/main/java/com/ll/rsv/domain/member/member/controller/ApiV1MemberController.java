package com.ll.rsv.domain.member.member.controller;


import com.ll.rsv.domain.member.member.dto.MemberDto;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/members", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Tag(name = "ApiV1MemberController", description = "회원 CRUD 컨트롤러")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;


    public record LoginRequestBody(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponseBody(@NonNull MemberDto item) {
    }

    @PostMapping(value = "/login")
    @Operation(summary = "로그인, accessToken, refreshToken 쿠키 생성됨")
    public RsData<LoginResponseBody> login(@Valid @RequestBody LoginRequestBody body) {
        RsData<MemberService.AuthAndMakeTokensResponseBody> authAndMakeTokensRs = memberService.authAndMakeTokens(
                body.username,
                body.password
        );

        rq.setCrossDomainCookie("refreshToken", authAndMakeTokensRs.getData().refreshToken());
        rq.setCrossDomainCookie("accessToken", authAndMakeTokensRs.getData().accessToken());

        return authAndMakeTokensRs.newDataOf(
                new LoginResponseBody(
                        new MemberDto(authAndMakeTokensRs.getData().member())
                )
        );
    }


    public record MeResponseBody(@NonNull MemberDto item) {
    }


    @GetMapping(value = "/me", consumes = ALL_VALUE)
    @Operation(summary = "내 정보")
    @SecurityRequirement(name = "bearerAuth")
    public RsData<MeResponseBody> getMe() {
        return RsData.of(
                new MeResponseBody(
                        new MemberDto(rq.getMember())
                )
        );
    }


    @PostMapping(value = "/logout", consumes = ALL_VALUE)
    @Operation(summary = "로그아웃")
    public RsData<Empty> logout() {
        rq.setLogout();

        return RsData.of("로그아웃 성공");
    }
}