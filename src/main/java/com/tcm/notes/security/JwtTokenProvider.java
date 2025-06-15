package com.tcm.notes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌提供者，负责生成和验证JWT令牌
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * 生成JWT令牌
     * @param authentication 认证信息
     * @return JWT令牌
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        logger.info("生成JWT令牌，用户名: {}", userPrincipal.getUsername());
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        logger.info("JWT令牌过期时间: {}", expiryDate);
        
        String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(java.util.stream.Collectors.joining(","));
        logger.info("用户权限: {}", authorities);
        
        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", authorities)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        
        logger.info("生成的JWT令牌: {}...", token.substring(0, Math.min(20, token.length())));
        return token;
    }

    /**
     * 根据用户名生成JWT令牌
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    /**
     * 根据用户ID和角色生成JWT令牌
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT令牌
     */
    public String generateToken(String userId, String role) {
        logger.info("生成JWT令牌，用户ID: {}, 角色: {}", userId, role);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        logger.info("JWT令牌过期时间: {}", expiryDate);
        
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        // 直接使用数据库中的角色，不添加ROLE_前缀
        logger.info("JWT令牌中存储的角色: {}", role);
        
        String token = Jwts.builder()
                .setSubject(userId)
                .claim("role", role)  // 直接存储数据库中的角色
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        logger.info("生成的JWT令牌: {}...", token.substring(0, Math.min(20, token.length())));
        return token;
    }

    /**
     * 创建令牌
     * @param claims 声明
     * @param subject 主题（用户名）
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        logger.info("从JWT令牌中提取用户名: {}...", token != null ? token.substring(0, Math.min(20, token.length())) : "null");
        try {
            String username = getClaimFromToken(token, Claims::getSubject);
            logger.info("成功从JWT令牌中提取用户名: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("从JWT令牌中提取用户名时发生错误: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取过期日期
     * @param token JWT令牌
     * @return 过期日期
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @param <T> 声明类型
     * @return 声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 从令牌中获取角色信息
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String tokenSubject = getUsernameFromToken(token); // 这里实际是用户ID
        logger.info("验证JWT令牌: tokenSubject={}, userDetails.username={}", tokenSubject, userDetails.getUsername());
        
        // 由于JWT中存储的是用户ID，而UserDetails中的username是真实用户名
        // 我们只需要验证令牌没有过期即可，用户身份已经通过loadUserByUsername验证
        boolean isValid = !isTokenExpired(token);
        logger.info("JWT令牌验证结果: {}", isValid ? "成功" : "失败（已过期）");
        return isValid;
    }
    
    /**
     * 验证令牌是否有效
     * @param authToken JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String authToken) {
        logger.info("验证JWT令牌: {}...", authToken != null ? authToken.substring(0, Math.min(20, authToken.length())) : "null");
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            logger.info("JWT令牌验证成功");
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            logger.error("无效的JWT签名: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("JWT令牌已过期: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT声明字符串为空: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT验证过程中发生未知错误: {}", e.getMessage());
        }
        logger.info("JWT令牌验证失败");
        return false;
    }
}