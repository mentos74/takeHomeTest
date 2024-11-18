package com.TakeHomeTest.restApi.util;
import com.TakeHomeTest.restApi.entity.User;
import com.TakeHomeTest.restApi.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserRepository userRepository;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 12;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }


    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }


    public void validateToken(String authorizationHeader) {

        if (authorizationHeader == null ||  (authorizationHeader!=null && (!authorizationHeader.startsWith("Bearer ") || authorizationHeader.equalsIgnoreCase("")))) {
            throw new io.jsonwebtoken.JwtException("Token tidak valid atau kadaluwarsa");
        }

        String token = "";
        String email = "";

        if(authorizationHeader!=null && authorizationHeader.length()>0){
           token = authorizationHeader.substring(7);
           email = extractEmail(authorizationHeader);
        }

        if (token==null || token.equalsIgnoreCase("")) {
            throw new io.jsonwebtoken.JwtException("Token tidak valid atau kadaluwarsa");
        }

        if(email!=null){
            Optional<User> user = userRepository.findByEmail(email);
            if (!user.isPresent()) {
                throw new io.jsonwebtoken.JwtException("Token tidak valid atau kadaluwarsa");
            }
        }else {
            throw new io.jsonwebtoken.JwtException("Token tidak valid atau kadaluwarsa");
        }


    }




}
