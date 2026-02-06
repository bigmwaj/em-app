package ca.bigmwaj.emapp.as.api.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    private static final String BASIC_PREFIX = "Basic ";

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            var authParam = request.getHeader("Authorization");
            if (!StringUtils.hasText(authParam)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authParam.startsWith(BEARER_PREFIX)) {
                processBearerAuthentication(request, response, filterChain);
            } else if (authParam.startsWith(BASIC_PREFIX)) {
                processBasicAuthentication(request, response, filterChain);
            } else {
                logger.warn("Unhandled authorization type");
                throw new RuntimeException("Unhandled authorization type");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Unable to get JWT Token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT Token has expired");
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
    }

    private void processBearerAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authParam = request.getHeader("Authorization");
        var jwt = authParam.substring(BEARER_PREFIX.length());

        if (tokenProvider.validateToken(jwt)) {
            var username = tokenProvider.getUsernameFromJWT(jwt);
            var authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private void processBasicAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authParam = request.getHeader("Authorization");
        var encodedParams = authParam.substring(BASIC_PREFIX.length());
        var decodedParams = new String(Base64.getDecoder().decode(encodedParams)).split(":");
        var username = decodedParams[0];
        var authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}