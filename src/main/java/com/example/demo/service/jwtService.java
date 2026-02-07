
package com.example.demo.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;


@Service
public class jwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private Key signingKey;
    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(signingKey, SignatureAlgorithm.HS256) // Pass the Key object
                .compact();
    }
    public String extractEmail(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true; // If parsing succeeds, token is valid
        } catch (Exception e) {
            return false; // Expired or tampered token
        }
    }
    
}
