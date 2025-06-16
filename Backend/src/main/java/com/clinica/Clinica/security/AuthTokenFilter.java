// src/main/java/com/clinica/Clinica/security/JwtAuthTokenFilter.java
package com.clinica.Clinica.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clinica.Clinica.service.UserDetailsServiceImpl;

/**
 * Este filtro será executado uma vez por requisição (OncePerRequestFilter),
 * então ele já implementa jakarta.servlet.Filter internamente.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 1) Extrai o token do cabeçalho Authorization (Bearer <token>)
        String headerAuth = request.getHeader("Authorization");
        String jwt = null;

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        // 2) Se houver token e ele for válido, extrai o username do token
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            // 3) Carrega os detalhes do usuário (UserDetails)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4) Cria um objeto de autenticação e coloca no SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5) Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
