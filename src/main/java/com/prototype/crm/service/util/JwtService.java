package com.prototype.crm.service.util;

import com.prototype.crm.model.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

//    @Value("${jwt.acces-token.expired-minutes:8}")
//    private long accessTokenExpiryMinutes;

//    @Value("${jwt.refresh-token.expiry-minutes:10080}")
//    private long refreshTokenExpiryMinutes;

    private Key getSignInKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Users user, int expiryMinutes) {
        return buildToken(user, expiryMinutes);
    }

    public String generateRefreshToken(Users user) {
        return buildToken(user, 60);
    }

    private String buildToken(Users user, int expiryMinutes) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId().toString());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * expiryMinutes))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public enum TokenStatus { VALID, EXPIRED, INVALID }

    public record TokenValidationResult(TokenStatus status, String subject, Claims claims) {
        public boolean isValid()  { return status == TokenStatus.VALID; }
        public boolean isExpired() { return status == TokenStatus.EXPIRED; }
    }

    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return new TokenValidationResult(TokenStatus.VALID, claims.getSubject(), claims);
        } catch (ExpiredJwtException e) {
            return new TokenValidationResult(TokenStatus.EXPIRED, null, null);
        } catch (JwtException | IllegalArgumentException e) {
            return new TokenValidationResult(TokenStatus.INVALID, null, null);
        }
    }

    public boolean isTokenValid(String token, Users user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
