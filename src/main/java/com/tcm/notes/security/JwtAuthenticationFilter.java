package com.tcm.notes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器，用于拦截请求并验证JWT令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取原始请求路径
        String requestPath = request.getRequestURI();
        // 获取应用上下文路径
        String contextPath = request.getContextPath();
        
        // 添加详细的调试日志
        logger.info("原始请求路径: " + requestPath + ", 上下文路径: " + contextPath);
        
        // 移除上下文路径前缀，以便正确匹配
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
            logger.info("移除上下文路径后的请求路径: " + requestPath);
        }
        
        // 跳过对公共资源的处理
        if (requestPath.startsWith("/public") || 
            requestPath.startsWith("/api/public") || 
            requestPath.equals("/api/public/passages") || // 明确添加这个路径
            requestPath.startsWith("/auth") || 
            requestPath.equals("/") || 
            requestPath.equals("/index.html") || 
            requestPath.startsWith("/static") || 
            requestPath.startsWith("/assets") || 
            requestPath.startsWith("/css") || 
            requestPath.startsWith("/js") || 
            requestPath.startsWith("/images") || 
            requestPath.equals("/favicon.ico")) {
            logger.info("跳过认证的公共路径: " + requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        // 增加调试日志，输出所有请求头信息
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headersLog = new StringBuilder("请求头信息: ");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headersLog.append(headerName).append(": ").append(request.getHeader(headerName)).append("; ");
        }
        logger.info(headersLog.toString());
        
        // 添加调试日志
        logger.info("需要认证的请求: " + request.getRequestURI() + ", 处理后路径: " + requestPath);
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("无法设置用户认证", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT令牌
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}