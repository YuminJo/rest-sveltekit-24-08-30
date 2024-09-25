package com.ll.rsv.domain.member.member.controller;


import com.ll.rsv.domain.member.member.dto.MemberDto;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}