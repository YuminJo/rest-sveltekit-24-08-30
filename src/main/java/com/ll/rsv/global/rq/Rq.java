package com.ll.rsv.global.rq;

import com.ll.rsv.global.app.AppConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;


    public void setStatusCode(int statusCode) {
        resp.setStatus(statusCode);
    }


    public String getCurrentUrlPath() {
        return req.getRequestURI();
    }


    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain(AppConfig.getSiteCookieDomain());
        resp.addCookie(cookie);
    }

    public void setCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        resp.addCookie(cookie);
    }

    private String getSiteCookieDomain() {
        String cookieDomain = AppConfig.getSiteCookieDomain();

        if (!cookieDomain.equals("localhost")) {
            return cookieDomain = "." + cookieDomain;
        }

        return null;
    }

    public void setCrossDomainCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain(getSiteCookieDomain())
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public void removeCrossDomainCookie(String name) {
        removeCookie(name);

        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .maxAge(0)
                .domain(getSiteCookieDomain())
                .secure(true)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public Cookie getCookie(String name) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }

        return null;
    }

    public String getCookieValue(String name, String defaultValue) {
        Cookie cookie = getCookie(name);

        if (cookie == null) {
            return defaultValue;
        }

        return cookie.getValue();
    }

    private long getCookieAsLong(String name, int defaultValue) {
        String value = getCookieValue(name, null);

        if (value == null) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    public void removeCookie(String name) {
        Cookie cookie = getCookie(name);

        if (cookie == null) {
            return;
        }

        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }


    public boolean isApi() {
        String xRequestedWith = req.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }
}