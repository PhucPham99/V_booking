package com.vbooking.backend.infrastructure.security;

import com.vbooking.backend.modules.user.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.jwt.refresh-expiration-milliseconds}")
    private long refreshExpirationDate;

    // 1. Tạo Access Token từ Authentication (Khi Login thành công)
    // Trong JwtTokenProvider.java

    // Sửa lại hàm này để nhận thêm tokenId
    public String generateAccessToken(UserEntity user, String tokenId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", "ROLE_" + user.getRole().toUpperCase());

        // QUAN TRỌNG: Nhúng ID của Refresh Token vào đây
        claims.put("tokenId", tokenId);

        return buildToken(claims, user.getEmail(), jwtExpirationDate);
    }

    // Thêm hàm lấy tokenId từ chuỗi JWT
    public String getTokenIdFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("tokenId", String.class);
    }

    // 1.1 Tạo Access Token từ UserEntity (Dùng khi Refresh Token)
    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", "ROLE_" + user.getRole().toUpperCase());

        return buildToken(claims, user.getEmail(), jwtExpirationDate);
    }

    // 2. Tạo Refresh Token
    public String generateRefreshToken(UserEntity user) {
        return buildToken(new HashMap<>(), user.getEmail(), refreshExpirationDate);
    }

    private String buildToken(Map<String, Object> extraClaims, UserPrincipal userPrincipal, long expiration) {
        // Tự động thêm userId vào claims nếu chưa có
        if (!extraClaims.containsKey("userId")) {
            extraClaims.put("userId", userPrincipal.getId());
        }
        return buildToken(extraClaims, userPrincipal.getUsername(), expiration);
    }

    // Hàm build chung (Overload)
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims) // Các thông tin thêm (UserId, Role...)
                .setSubject(subject) // Username hoặc Email
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời điểm tạo
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Thời điểm hết hạn
                .signWith(key(), SignatureAlgorithm.HS256) // Ký tên
                .compact();
    }
    // ==================================================================

    // 3. Lấy Username từ Token
    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 4. Validate Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}