package com.sai.todo.security;
import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService=jwtService;

    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException,IOException{

            String authHeader = request.getHeader("Authorization");
if (authHeader == null) {
    filterChain.doFilter(request, response);
    return;
}

if (!authHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}


String jwt = authHeader.substring(7);
try{
 Claims claims=jwtService.extractAllClaims(jwt);
 String username=claims.getSubject();
  String role=claims.get("role",String.class);
  SimpleGrantedAuthority authority=new SimpleGrantedAuthority("ROLE_"+role);
   UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(username,null,List.of(authority));
     SecurityContextHolder.getContext().setAuthentication(authentication);

}
catch(Exception e){
     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
     return;

}

filterChain.doFilter(request, response);
}
    

}
