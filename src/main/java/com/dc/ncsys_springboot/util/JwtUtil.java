package com.dc.ncsys_springboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // 生成Token
    public String genToken(Map<String, Object> claims) {
        return JWT.create()
                .withPayload(claims) // 直接平铺Claims到JWT声明
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 1000 * 60))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }

    // 解析Token
    public Map<String, Object> parseToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                    .build()
                    .verify(token)
                    .getClaims()
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().as(Object.class)
                    ));
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token验证失败", e);
        }
    }
}
