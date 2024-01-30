package com.demogroup.demoweb.utils;


import com.demogroup.demoweb.domain.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

//UsernamePasswordAuthenticationFilter를 상속받는 LoginFilter와는 다르게
//이 클래스는 javadoc에서 관리시켜주기 위해 @Component 어노테이션을 붙여준다.
@Component
public class JWTUtils {

    private Key key;
    private final RedisTemplate<String,String> redisTemplate;

    @Autowired
    public JWTUtils(@Value("${spring.jwt.secret}")String secretKey, RedisTemplate<String,String> redisTemplate){
        //secretkey를 암호화한다.
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(decodedKey);
        this.redisTemplate=redisTemplate;
    }

    public String getName(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("name",String.class);
    }

    public String getUsername(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("username",String.class);
    }

    public String getNickname(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname",String.class);
    }

    public String getEmail(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("email",String.class);
    }

    public String getRole(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role",String.class);
    }

    /*이 JWTUtil 에서는 토큰을 생성할 때, user 정보가 담긴 CustomUserDetails를 받고, Long 타입인 expiredMs를 받는다.
    */
    public String createToken(CustomUserDetails dto, Long expiredMs){
        String role = dto.getAuthorities().iterator().next()
                .getAuthority();

        Claims claims= Jwts.claims();
        claims.put("name",dto.getName());
        claims.put("username",dto.getUsername());
        claims.put("nickname",dto.getNickname());
        claims.put("email",dto.getEmail());
        claims.put("role",role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public Long duration(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().getTime()-new Date().getTime();
    }

    //redis 블랙리스트에 key는 token, value는 날짜, duration을 계산하여 올린다.
    public String logout(String token){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Duration duration = Duration.ofSeconds(duration(token));
        operations.set(token,new Date().toString(),duration);
        return token;
    }

}
