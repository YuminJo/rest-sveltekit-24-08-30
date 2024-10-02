package com.ll.rsv.global.security;

import com.ll.rsv.domain.member.member.service.AuthTokenService;
import com.ll.rsv.global.rq.Rq;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Rq rq;
    private final AuthTokenService authTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String redirectUrlAfterSocialLogin = rq.getCookieValue("redirectUrlAfterSocialLogin", "");

        // 일반적인 방식이 아닌 외부 프론트도메인에서 요청한 소셜 로그인인 경우에는 아래와 같이 처리한다.
        if (rq.isFrontUrl(redirectUrlAfterSocialLogin)) {
            String accessToken = authTokenService.genAccessToken(rq.getMember());
            String refreshToken = rq.getMember().getRefreshToken();

            rq.destroySession(); // 기본적으로 세션에 회원정보를 넣어준다. 이 경우에는 그럴 필요가 없어서 지운다.
            rq.setCrossDomainCookie("accessToken", accessToken);
            rq.setCrossDomainCookie("refreshToken", refreshToken);
            rq.removeCookie("redirectUrlAfterSocialLogin");

            response.sendRedirect(redirectUrlAfterSocialLogin);
            return; // 필수, 이게 없으면 이후의 디폴트 처리로 넘어간다.
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}