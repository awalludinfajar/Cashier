package com.test.aegis.config;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.test.aegis.dto.AuthData;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtilsConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtilsConfig.class);

    @Value("${jwt.refreshExpirationMs}")
    private int refreshJwtExpirationMs;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private String algorithm = "HS256";

    public boolean validate(String authToken) {
        Key hmaKye = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS256.getJcaName());
        try {
            Jwts.parserBuilder()
                .setSigningKey(hmaKye)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String generateJwtToken(Authentication authentication, Map<String, Object> additionalAttributes) {
        AuthData principal = (AuthData) authentication.getPrincipal();

        return Jwts.builder().setSubject(principal.getFullName())
                   .setIssuer("self")
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                   .signWith(new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS256.getJcaName()))
                   .setHeaderParam("ALGORITHM", algorithm)
                   .setHeaderParam("TOKEN_TYPE", "ACCESS_TOKEN")
                   .claim("username", principal.getUsername())
                   .setClaims(additionalAttributes)
                   .compact();
    }

    public String generateRefreshJwtToken(Authentication authentication, Map<String, Object> additionalAttributes) {
        AuthData principal = (AuthData) authentication.getPrincipal();

        return Jwts.builder().setSubject(principal.getFullName())
                .setIssuer("self")
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS256.getJcaName()))
                .setHeaderParam("ALGORITHM", algorithm)
                .setHeaderParam("TOKEN_TYPE", "ACCESS_TOKEN")
                .claim("username", principal.getUsername())
                .setClaims(additionalAttributes)
                .compact();
    }

    public String getUsernamelFromJwtToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS256.getJcaName()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username");
    }
}
