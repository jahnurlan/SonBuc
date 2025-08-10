package com.example.workspacems.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(1)
@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Örnek: 30 istek / 1 dakika
        Bandwidth limit = Bandwidth.classic(150, Refill.greedy(150, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(javax.servlet.ServletRequest request,
                         javax.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(429); // Too Many Requests
            res.getWriter().write("Çok fazla istek yaptınız. Lütfen biraz bekleyin.");
            return;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // opsiyonel
    }

    @Override
    public void destroy() {
        // opsiyonel
    }
}

