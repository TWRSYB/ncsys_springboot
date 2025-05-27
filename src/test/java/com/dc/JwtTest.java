package com.dc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testGen() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("userName", "张三");
//        生成jwt的代码
        String token = JWT.create()
                .withClaim("user", claims) // 添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 添加过期时间 1小时
                .sign(Algorithm.HMAC256("itheima"));// 指定算法, 配置密钥

        System.out.println("token = " + token);
    }

    @Test
    public void testParse() {

    }
}
