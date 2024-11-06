package com.ll.rsv.domain.member.member.controller;

import com.ll.rsv.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final Rq rq;

    @GetMapping("/login")
    @ResponseBody
    public String login() {
        return """
                <form method="POST">
                    <div>
                        <label>아이디</label>
                        <input type="text" name="username">
                    </div>
                    <div>
                        <label>비밀번호</label>
                        <input type="password" name="password">
                    </div>
                    <div>
                        <button type="submit">로그인</button>
                    </div>
                </form>
                """;
    }

    @GetMapping("/socialLogin/{providerTypeCode}")
    public String socialLogin(String redirectUrl, @PathVariable String providerTypeCode) {
        if (rq.isFrontUrl(redirectUrl)) {
            rq.setCookie("redirectUrlAfterSocialLogin", redirectUrl, 60 * 10);
        }

        return "redirect:/oauth2/authorization/" + providerTypeCode;
    }
}