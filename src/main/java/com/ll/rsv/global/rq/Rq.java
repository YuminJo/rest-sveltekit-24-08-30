package com.ll.rsv.global.rq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    public boolean isApi() {
        String xRequestedWith = req.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }
    public void setStatusCode(int statusCode) {
        resp.setStatus(statusCode);
    }
    public String getCurrentUrlPath() {
        return req.getRequestURI();
    }
}