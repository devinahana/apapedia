package com.apapedia.user.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.apapedia.user.dto.response.BaseResponse;
import com.apapedia.user.security.service.UserDetailsServiceImpl;
import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, NoSuchElementException {
        try {
            String uri = request.getRequestURI();
            if (!(uri.equals("/api/user/login")
                    || uri.equals("/api/user/register")
                    || uri.equals("/favicon.ico")
                    || uri.startsWith("/swagger-ui")
                    || uri.startsWith("/v3/api-docs")
            )) {
                String jwt = parseJwt(request);
                if (jwt == null || jwt.isEmpty()) {
                    throw new IllegalArgumentException("JWT String argument cannot be null or empty.");
                } else if (!jwtUtils.validateJwtToken(jwt)) {
                    throw new IllegalArgumentException("JWT token is invalid");
                }
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String userId = jwtUtils.getUserIDFromJwtToken(jwt);
                request.setAttribute("userId", userId);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (NoSuchElementException e) {
            logger.error("User not found: " + e.getMessage());
            String responseJsonString = this.gson.toJson(new BaseResponse<>(false, "User not found."));
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(responseJsonString);
            out.flush();
            return;
        } catch (Exception e) {
            String errorResponse = "Cannot set user authentication: " + e.getMessage();
            logger.error(errorResponse);
            String responseJsonString = this.gson
                    .toJson(new BaseResponse<>(false, errorResponse));
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(responseJsonString);
            out.flush();
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

}
