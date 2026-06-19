package com.arctic.equipment.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 基于令牌桶算法的接口限流过滤器
 * 
 * 数学原理：
 *   设水桶最大容量为 V（最大突发并发量），令牌填入速率为 r（个/秒）。
 *   当前请求到达时间 t_n，上一次请求到达时间 t_{n-1}，
 *   桶中令牌数：N(t_n) = min(V, N(t_{n-1}) + r × (t_n - t_{n-1}))
 * 
 *   若 N(t_n) ≥ 1，请求通过，令牌数减一；否则返回 429 (Too Many Requests)。
 * 
 * 拦截路径：/order/* （通过 web.xml 配置，保证在 LoginFilter 之前执行）
 * 
 * Filter 执行顺序（web.xml 中声明）：
 *   EncodingFilter(/*) → RateLimitFilter(/order/*) → LoginFilter(/order/*)
 */
public class RateLimitFilter implements Filter {

    /** 令牌桶最大容量（允许的最大突发并发量） */
    private static final double MAX_CAPACITY = 50.0;

    /** 令牌生成速率（个/秒）—— 常态 QPS 上限 */
    private static final double REFILL_RATE = 10.0;

    /** 当前令牌数（使用 double 以保证小数精度计算） */
    private double tokens = MAX_CAPACITY;

    /** 上次补充令牌的时间戳（使用纳秒保证不受系统时钟调整影响） */
    private long lastRefillTime = System.nanoTime();

    /**
     * 尝试消费一个令牌
     * 
     * 并发安全：synchronized 保证多线程下令牌桶状态的一致性
     * 
     * @return true 表示令牌充足，请求放行；false 表示令牌不足，请求拦截
     */
    private synchronized boolean tryConsume() {
        long now = System.nanoTime();
        // 计算自上次补充以来经过的秒数（双精度浮点）
        double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;

        // 按公式补充令牌：N(t) = min(V, N_old + r × Δt)
        double newTokens = elapsedSeconds * REFILL_RATE;
        tokens = Math.min(MAX_CAPACITY, tokens + newTokens);
        lastRefillTime = now;

        // 判断是否允许通过
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("====== RateLimitFilter 已启动 (令牌桶容量=" + MAX_CAPACITY + ", 速率=" + REFILL_RATE + "/s) ======");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (tryConsume()) {
            // 令牌充足，放行到下一个 Filter 或 Servlet
            chain.doFilter(request, response);
        } else {
            // 令牌不足，拦截请求并返回 429
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"系统繁忙，请稍后再试 (QPS限流: " + REFILL_RATE + "/s)\"}"
            );
        }
    }

    @Override
    public void destroy() {
        System.out.println("====== RateLimitFilter 已销毁 ======");
    }
}