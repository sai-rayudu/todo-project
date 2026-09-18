package com.sai.todo.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;



@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}

public String generateToken(String username,String role) {
    return Jwts.builder()
            .subject(username)
            .claim("role",role)
                  .issuedAt(new Date())
                       .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}



public Claims extractAllClaims(String token){
    return Jwts.parser().verifyWith((SecretKey) getSigningKey())
    .build().parseSignedClaims(token)
    .getPayload();
}

}
