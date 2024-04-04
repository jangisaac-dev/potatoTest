package dev.hsu.potatotest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtTokenProvider {

//    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access_valid_time}")
    private String accessTokenValidSecond;
    @Value("${jwt.refresh_valid_time}")
    private String refreshTokenValidSecond;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    public JwtTokenProvider() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
//    }

    public String createAccessToken(Long userId) {
        return createToken(String.valueOf(userId), accessTokenValidSecond);
    }
    public String createRefreshToken(Long userId) {
        return createToken(String.valueOf(userId), refreshTokenValidSecond);
    }

    public String createToken(String userId, String validTime){
        Claims claims = Jwts.claims().subject(userId).build();
        Date now = new Date();

        System.out.println("validTime : " + validTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(Long.parseLong(validTime) * 1000 + now.getTime()))
                .signWith(getSigningKey())
                .compact();
    }


    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String getUserId(String token){
        return extractAllClaims(token).getSubject();
    }
}