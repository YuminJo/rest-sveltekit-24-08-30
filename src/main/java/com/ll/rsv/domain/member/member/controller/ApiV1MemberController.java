package com.ll.rsv.domain.member.member.controller;


import com.ll.rsv.domain.member.member.dto.MemberDto;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    
    public record LoginRequestBody(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponseBody(@NonNull MemberDto item) {
    }

    @PostMapping("/login")
    public RsData<LoginResponseBody> login(@Valid @RequestBody LoginRequestBody body) {
        // 이 코드와 같이 서비스에서도 필요하다면 RsData를 사용할 수 있다.
        RsData<MemberService.AuthAndMakeTokensResponseBody> authAndMakeTokensRs = memberService.authAndMakeTokens(
                body.username,
                body.password
        );

        rq.setCrossDomainCookie("refreshToken", authAndMakeTokensRs.getData().refreshToken());
        rq.setCrossDomainCookie("accessToken", authAndMakeTokensRs.getData().accessToken());

        // newDataOf 는 authAndMakeTokensRs 객체와 똑같지만 data 만 다른 새 객체를 생성해서 반환한다.
        // 즉 새 RsData 객체를 만들고 싶은데 내가 원하는 새 객체와 data 만 빼고 다른 부분은 일치하는 객체가 이미 있을때 사용하면 좋다.
        return authAndMakeTokensRs.newDataOf(
                new LoginResponseBody(
                        new MemberDto(authAndMakeTokensRs.getData().member())
                )
        );
    }

    
    public record MeResponseBody(@NonNull MemberDto item) {
    }

    @GetMapping("/me")
    public RsData<MeResponseBody> getMe() {
        return RsData.of(
                new MeResponseBody(
                        // 여기서 rq.getMember() 를 하면
                        // API 요청시에 같이 딸려온 쿠키로 부터 생생되고 SecurityContext 에 등록된 SecurityUser 객체를 통해서 만들어진 Member(프록시) 객체가 반환된다.
                        // rq.getMember() 가 작동되게 하려고 JwtAuthenticationFilter 를 추가하고 구현한 것이다.
                        new MemberDto(rq.getMember())
                )
        );
    }

    @PostMapping("/logout")
    public RsData<Empty> logout() {
        // 로그아웃은 브라우저에서 쿠키를 버리고 rq.member 를 초기화 하면 가능하다.
        // 원래 쿠키는 서버와 클라이언트에서 둘 다 편집이 가능하다.
        // 하지만 이렇게 서버에 요청까지 하는 이유는
        // refreshToken, accessToken 이 HTTP ONLY 쿠키라서 브라우저에서 접근/조회/수정/삭제가 불가능하다.
        // 그래서 어쩔 수 없이 서버측에 삭제요청을 하면 이렇게 아래에서 처럼 지워는 형식을 취한다.
        rq.setLogout();

        return RsData.of("로그아웃 성공");
    }
}