
package com.uasjava.tiketbioskop.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uasjava.tiketbioskop.dto.UserCredentialsDto;
import com.uasjava.tiketbioskop.provider.JwtProvider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request , 
                                    HttpServletResponse response,
                                    FilterChain filterChain)  
                                    throws IOException, ServletException{
        
        try {
            String accessToken = jwtProvider.resolveToken(request);
            if(accessToken == null){
                filterChain.doFilter(request, response);
                return;
            }


            if (jwtProvider.validateToken(accessToken)) {
                Claims claims = jwtProvider.getAllClaimsFromToken(accessToken);
                String username = jwtProvider.getUsernameFromToken(accessToken);
                Integer userId = jwtProvider.getUserIdFromToken(accessToken);
                List<String> roles = jwtProvider.getRolesFromToken(accessToken);

                if (username != null && userId != null && roles != null) {
                    UserCredentialsDto credentialPayload = new UserCredentialsDto();
                    credentialPayload.setUserId(userId);
                    credentialPayload.setUsername(username);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        credentialPayload,
                        roles.stream()
                            .filter(role -> role != null && !role.trim().isEmpty())
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .toList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Invalid token claims: username={}, userId={}, roles={}", username, userId, roles);
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\", \"message\": \"JWT token has expired\"}");
            return;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\", \"message\": \"JWT token is malformed\"}");
            return;
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\", \"message\": \"JWT signature is invalid\"}");
            return;
        } catch (Exception e) {
            log.error("JWT Filter error: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal server error\", \"message\": \"Authentication service error\"}");
            return;
        }

        filterChain.doFilter(request, response);
        
    }
}
