package com.example.sparepartsinventorymanagement.jwt;

import com.example.sparepartsinventorymanagement.jwt.userprincipal.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailService userDetailService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.getJwt(request);
            if (token != null && jwtProvider.validateToken(token) ){
                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                if(userDetails.isEnabled() && userDetails.isAccountNonExpired() && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (Exception e){
            logger.error("Can't set user authentication -> Message: ", e);
        }
        filterChain.doFilter(request, response);
    }

}
