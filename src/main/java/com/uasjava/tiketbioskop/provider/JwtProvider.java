package com.uasjava.tiketbioskop.provider;


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.validity}")
    private Long accessTokenValidity;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    private JwtParser jwtParser;

    @PostConstruct
    public void init(){
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String generateToken(Integer userId, String username, List<String> role) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.setId(String.valueOf(userId));
        claims.put("username", username);
        claims.put("authorities", role);

        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));

        return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(tokenValidity)
                    .signWith(SignatureAlgorithm.HS512, secretKey) // ini HS512 untuk algoritma tokennya
                    .compact();
    }


    public String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(header);
        if(bearerToken != null && bearerToken.startsWith(prefix)){
            return bearerToken.substring(prefix.length());
        }

        return null;
    }


    public boolean validateClaims(Claims claims){
        return claims.getExpiration().after(new Date());
    }

    private Claims parseJwtClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req){

        try{
            String token = resolveToken(req);
            if(token != null){
                return parseJwtClaims(token);
            }
            return null;

        }catch(ExpiredJwtException ex){
            req.setAttribute("expired", ex.getMessage());
            throw ex;

        }catch(Exception ex){
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }


}

