package com.jobfirm.userservice.filter;

import com.jobfirm.common.config.JobFirmProperties;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InternalAccessFilter implements Filter {
    private final JobFirmProperties jobFirmProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String secret = req.getHeader("X-Internal-Secret");

        if (secret == null || !secret.equals(jobFirmProperties.getInternalSecret()) ) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"code\":403,\"msg\":\"Forbidden: Gateway only\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
