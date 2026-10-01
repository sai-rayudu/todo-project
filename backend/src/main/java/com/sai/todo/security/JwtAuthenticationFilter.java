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

import com.sai.todo.entity.User;
import com.sai.todo.repository.UserRepository;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Optional;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    public JwtAuthenticationFilter(JwtService jwtService,UserRepository userRepository){
        this.jwtService=jwtService;
        this.userRepository=userRepository;

    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException,IOException{

            String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}




String jwt = authHeader.substring(7);
try{
 Claims claims=jwtService.extractAllClaims(jwt);
 String username=claims.getSubject();

 Optional<User> userOptional=userRepository.findByUsername(username);
 if(userOptional.isEmpty()){
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
 }
 User user=userOptional.get();

 int tokenVersion=claims.get("tokenVersion",Integer.class);
 if(tokenVersion!=user.getTokenVersion()){
     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
     return;
 }

  String role=user.getRole();
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
