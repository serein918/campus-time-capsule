package com.capsule.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    // 使用固定密钥（至少32字节），避免服务重启后旧Token全部失效
    private static final String SECRET = "CapsuleTimeCapsuleSecretKey2026!";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token 有效期 24 小时
    private static final long EXPIRATION = 86400000L;

    /**
     * 生成 JWT Token
     */
    public static String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 JWT Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
