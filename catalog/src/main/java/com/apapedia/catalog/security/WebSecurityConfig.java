package com.apapedia.catalog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.apapedia.catalog.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
        @Autowired
        private JwtTokenFilter jwtTokenFilter;

        @Bean
        @Order(1)
        public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
                http.securityMatcher("/api/**")
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/catalog").hasAuthority("SELLER")
                                                .requestMatchers(HttpMethod.PUT, "/api/catalog/{catalogId}")
                                                .hasAuthority("SELLER")
                                                .requestMatchers(HttpMethod.DELETE, "/api/catalog/{catalogId}")
                                                .hasAuthority("SELLER")
                                                .anyRequest().authenticated())
                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}