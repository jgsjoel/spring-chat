package com.chat.backend.services;

import com.chat.backend.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt_issuer}")
    private String issuer;
    @Value("${refresh-token-life-time}")
    private long refreshTokenTime;
    @Value("${jwt-token-life-time}")
    private long jwtTokenTime;
    @Value("${email-token-life-time}")
    private long emailTOmenTIme;
    @Value("${secret-string}")
    private String secretString;
    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    }

    private long getTokenTime(TokenType type){
        long expiration = 0;
        switch (type){
            case ACCESS_TOKEN:
                expiration = jwtTokenTime;
                break;
            case REFRESH_TOKEN:
                expiration = refreshTokenTime;
                break;
            case EMAIL_VERIFICATION_TOKEN:
                expiration = emailTOmenTIme;
        }
        return expiration;
    }

    public String generateToken(String email, TokenType type){
        return Jwts
                .builder()
                .issuer(issuer)
                .subject(email)
                .claim("tokenType",type)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(getTokenTime(type))))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token){
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public String getEmail(String token){
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isExpired(String token){
        try{
            Claims claims = getClaims(token);
            return claims.getExpiration().before(Date.from(Instant.now()));
        }catch (ExpiredJwtException e){
            return true;
        }
    }

    public String getTokenType(String token){
        Claims claims = getClaims(token);
        return claims.get("tokenType").toString();
    }

}
