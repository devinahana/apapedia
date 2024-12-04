package com.apapedia.order.security;

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
import com.apapedia.order.security.jwt.JwtTokenFilter;

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
                                                .requestMatchers(HttpMethod.PUT, "/api/order/change-status").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/order/monthly-sales/{sellerId}").hasAuthority("SELLER")
                                                .requestMatchers(HttpMethod.GET, "/api/order/seller/{sellerId}").hasAuthority("SELLER")
                                                .anyRequest().hasAuthority("CUSTOMER"))
                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
