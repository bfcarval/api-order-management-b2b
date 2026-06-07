package com.api.order.management.b2b.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        final var httpRequest = (HttpServletRequest) request;
        final var httpResponse = (HttpServletResponse) response;
        final var headerTraceId = httpRequest.getHeader("x-trace-id");
        final var traceId = (headerTraceId != null) ? headerTraceId : UUID.randomUUID().toString();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            httpResponse.addHeader("x-trace-id", traceId);

            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
