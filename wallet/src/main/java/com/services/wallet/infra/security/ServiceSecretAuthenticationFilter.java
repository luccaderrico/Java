package com.services.wallet.infra.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.preauth.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
@Component
public class ServiceSecretAuthenticationFilter implements Filter {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String[] EXCLUDED_PATHS = { "/health", "/docs", "/v3/api-docs/**", "/swagger-ui/**" };
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestURI = httpRequest.getRequestURI();
        for (String path : EXCLUDED_PATHS) {
            if (antPathMatcher.match(path, requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.equals(secret)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            return;
        }

        UserDetails userDetails = User.withUsername("service").password("").roles("SERVICE").build();
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
