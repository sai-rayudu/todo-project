package com.sai.todo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.beans.BeanProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${frontend.origin}")
     private String frontendOrigin;
   

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,JwtAuthenticationFilter jwtAuthenticationFilter){
        this.customUserDetailsService=customUserDetailsService;
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
     
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{

        return configuration.getAuthenticationManager();

        }
        
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
       
        return provider;
    }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http.cors(cors -> {}).csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth->auth
        .requestMatchers("/register","/login","/", "/privacy-policy").permitAll().requestMatchers("/admin/**")
        .hasRole("ADMIN").anyRequest().authenticated()).exceptionHandling(exception -> exception
        .accessDeniedHandler((request,response,accessDeniedException)->response.setStatus(HttpServletResponse.SC_FORBIDDEN)).authenticationEntryPoint(
            (request, response, authException) ->
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        )
    )
    .sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
).addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
);
    
   

   return http.build();
   }

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}


@Bean
public CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration configuration=new CorsConfiguration();

    configuration.setAllowedOrigins(List.of(frontendOrigin));
    configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**",configuration);

    return source;

}

    
    
}
