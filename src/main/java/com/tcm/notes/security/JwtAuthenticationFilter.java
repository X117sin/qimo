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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT认证过滤器，用于拦截请求并验证JWT令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("\n=== JWT过滤器被调用 ===");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("请求路径: " + request.getServletPath());
        System.out.println("查询字符串: " + request.getQueryString());
        System.out.println("=== JWT过滤器调用结束 ===\n");
        
        try {
            logger.info("=== JWT过滤器开始处理请求: {} {} ===", request.getMethod(), request.getRequestURI());
            logger.info("请求头信息: Authorization={}", request.getHeader("Authorization"));
            logger.info("Cookie信息: {}", request.getHeader("Cookie"));
            
            // 检查是否是公共资源或静态资源
            // 获取原始请求路径
            String requestPath = request.getRequestURI();
            // 获取应用上下文路径
            String contextPath = request.getContextPath();
            
            // 添加详细的调试日志
            logger.info("原始请求路径: {}, 上下文路径: {}", requestPath, contextPath);
            
            // 移除上下文路径前缀，以便正确匹配
            if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
                requestPath = requestPath.substring(contextPath.length());
                logger.info("移除上下文路径后的请求路径: {}", requestPath);
            }
            
            // 跳过对公共资源的处理
            if (requestPath.startsWith("/public") || 
                requestPath.startsWith("/api/public") || 
                requestPath.equals("/api/public/passages") || // 明确添加这个路径
                requestPath.startsWith("/auth") ||
                requestPath.equals("/api/auth/login") || // 明确跳过登录
                requestPath.equals("/api/auth/register") || // 明确跳过注册 
                requestPath.equals("/") || 
                requestPath.equals("/index.html") || 
                requestPath.equals("/login.html") || 
                requestPath.equals("/register.html") || 
                requestPath.equals("/passages.html") || 
                requestPath.equals("/create-admin.html") || 
                requestPath.equals("/admin-check.html") || 
                requestPath.equals("/admin-dashboard.html") || // 允许访问管理员仪表盘页面
                requestPath.equals("/dashboard.html") || // 允许访问用户仪表盘页面
                requestPath.equals("/charts.html") || // 允许访问图表页面
                requestPath.equals("/notes.html") || // 允许访问笔记页面
                requestPath.startsWith("/static") || 
                requestPath.startsWith("/assets") || 
                requestPath.startsWith("/css") || 
                requestPath.startsWith("/js") || 
                requestPath.startsWith("/images") || 
                requestPath.equals("/favicon.ico") ||
                requestPath.equals("/health")) {
                logger.info("跳过公共资源: {}", requestPath);
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
        logger.info("需要认证的请求: {}, 处理后路径: {}", request.getRequestURI(), requestPath);
        
        // 从请求中获取JWT令牌
        String jwt = getJwtFromRequest(request);
        logger.info("提取的JWT令牌: {}", (jwt != null ? jwt.substring(0, Math.min(10, jwt.length())) + "..." : "null"));

        // 如果令牌存在且有效
        if (StringUtils.hasText(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenProvider.getUsernameFromToken(jwt);
            
            // 添加JWT令牌解析日志
            logger.info("从JWT令牌中解析出用户名: {}", username);
            
            // 添加JWT令牌内容日志
            try {
                String role = jwtTokenProvider.getRoleFromToken(jwt);
                logger.info("从JWT令牌中解析出角色: {}", role);
                
                // 检查角色是否为null或空
                if (role == null || role.isEmpty()) {
                    logger.warn("警告: JWT令牌中的角色为空，这可能导致权限验证失败");
                }
                
                // 检查角色大小写
                if (role != null && !role.equals(role.toUpperCase())) {
                    logger.warn("警告: JWT令牌中的角色不是全大写: '" + role + "'，应为: '" + role.toUpperCase() + "'");
                }
            } catch (Exception e) {
                logger.error("从JWT令牌中解析角色失败: " + e.getMessage());
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 添加用户详情日志
            logger.info("加载的用户详情: 用户名={}, 角色={}", userDetails.getUsername(), userDetails.getAuthorities());
            
            // 验证JWT令牌中的角色与用户实际角色是否匹配
            try {
                String jwtRole = jwtTokenProvider.getRoleFromToken(jwt);
                boolean roleMatches = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(jwtRole));
                logger.info("JWT令牌角色验证: JWT角色={}, 用户角色={}, 匹配={}", 
                    jwtRole, userDetails.getAuthorities().toString(), roleMatches);
                
                if (!roleMatches) {
                    logger.warn("JWT令牌中的角色与用户实际角色不匹配，拒绝认证");
                    return;
                }
            } catch (Exception e) {
                logger.error("验证JWT令牌角色时发生错误: " + e.getMessage());
                return;
            }
            
            if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 添加认证成功日志
                logger.info("用户认证成功: {}, 权限: {}", username, userDetails.getAuthorities());
            } else {
                // 添加令牌验证失败日志
                logger.warn("JWT令牌验证失败: " + username);
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
        logger.info("Authorization头完整内容: [{}], 长度: {}", bearerToken, (bearerToken != null ? bearerToken.length() : "null"));
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.info("令牌长度: {}, 前10个字符: {}", token.length(), token.substring(0, Math.min(10, token.length())));
            return token;
        }
        
        // 如果Authorization头为空，尝试从Cookie中获取令牌
        logger.info("Authorization头验证失败，尝试从Cookie中提取令牌");
        logger.info("bearerToken为空: {}, 不以Bearer开头: {}", !StringUtils.hasText(bearerToken), bearerToken != null && !bearerToken.startsWith("Bearer "));
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    logger.info("提取的JWT令牌: {}...", token.substring(0, Math.min(10, token.length())));
                    return token;
                }
            }
        }
        
        logger.info("未找到JWT令牌");
        return null;
    }
}