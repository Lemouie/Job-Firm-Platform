package com.jobfirm.auth.service.impl;

import com.jobfirm.common.config.JwtProperties;
import com.jobfirm.common.auth.vo.TokenValidateVO;
import com.jobfirm.auth.service.AuthService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthServiceImpl(JwtProperties jwtProperties, RedisTemplate<String, Object> redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    private SecretKey getKey() {
        // HS256 要求 secret 必须满足32字节（256bit）, 否则会报：WeakKeyException
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    @Override
    public String generateToken(Long userId, String role) {

        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getExpire() * 1000);
        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();


        // 写入 Redis，设置过期时间
        redisTemplate.opsForValue().set(
                jwtProperties.getTokenCachePrefix() + token,
                true,
                30*60,
                TimeUnit.SECONDS
        );

        return token;
    }

    @Override
    public TokenValidateVO validateToken(String token) {

        TokenValidateVO vo = new TokenValidateVO();

        String key = jwtProperties.getTokenCachePrefix() + token;

        // 1. 解析 JWT（JWT 是最终权威）
        Claims claims;
        try {
            claims = parseToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired", e);
            vo.setValid(false);
            return vo;
        } catch (JwtException e) {
            log.error("Invalid token", e);
            vo.setValid(false);
            return vo;
        }

        vo.setValid(true);
        vo.setUserId(claims.getSubject());
        vo.setRole(claims.get("role", String.class));

        Long jwtRemaining = claims.getExpiration().getTime() - System.currentTimeMillis();

        // 2. 查询 Redis
        Boolean cached = (Boolean) redisTemplate.opsForValue().get(key);

        // 3. Redis 命中 → 滑动过期：刷新 TTL
        if (Boolean.TRUE.equals(cached)) {

            // 刷新 Redis TTL（滑动过期）
            redisTemplate.expire(
                    key,
                    30*60,
                    TimeUnit.SECONDS
            );

            // 如果 JWT 剩余 ≤ 30 分钟 → 自动续签
            if (jwtRemaining <= 30 * 60 * 1000) {
                vo.setNewToken(refreshToken(claims, token));
            }

            return vo;
        }

        // 4. Redis 未命中 → 写入 Redis（首次登录或缓存丢失）
        redisTemplate.opsForValue().set(
                key,
                true,
                30*60,
                TimeUnit.SECONDS
        );

        // JWT 剩余 ≤ 30 分钟 → 自动续签
        if (jwtRemaining <= 30 * 60 * 1000) {
            vo.setNewToken(refreshToken(claims, token));
        }

        return vo;
    }

    private String refreshToken(Claims claims, String oldToken) {

        // 生成新 token
        String newToken = generateToken(
                Long.valueOf(claims.getSubject()),
                claims.get("role", String.class)
        );

        // 删除旧 token
        redisTemplate.delete(jwtProperties.getTokenCachePrefix() + oldToken);

        // 写入新 token
        redisTemplate.opsForValue().set(
                jwtProperties.getTokenCachePrefix() + newToken,
                true,
                30*60,
                TimeUnit.SECONDS
        );
        return newToken;
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
