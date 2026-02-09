package ca.bigmwaj.emapp.as.api.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security configuration for the application.
 * 
 * <p>This configuration implements a stateless authentication architecture using:
 * <ul>
 *   <li>OAuth 2.0 for third-party authentication (Google, GitHub, Facebook, TikTok)</li>
 *   <li>JWT (JSON Web Tokens) for maintaining session state</li>
 *   <li>CORS configuration for cross-origin requests from the frontend</li>
 * </ul>
 * 
 * <h2>Authentication Flow:</h2>
 * <ol>
 *   <li>User initiates OAuth login via /oauth2/authorization/{provider}</li>
 *   <li>OAuth provider authenticates the user</li>
 *   <li>PostOAuth2Authentication service processes the OAuth response</li>
 *   <li>OAuth2AuthenticationSuccessHandler generates a JWT token</li>
 *   <li>Frontend receives the JWT and includes it in subsequent requests</li>
 *   <li>AuthenticationFilter validates the JWT on each protected request</li>
 * </ol>
 * 
 * <h2>Security Features:</h2>
 * <ul>
 *   <li><b>Stateless Sessions:</b> No server-side session storage, improving scalability</li>
 *   <li><b>JWT Authentication:</b> Tokens contain user identity and are cryptographically signed</li>
 *   <li><b>CSRF Protection Disabled:</b> Safe for JWT-based auth (tokens in headers, not cookies)</li>
 *   <li><b>CORS Configured:</b> Allows frontend (localhost:4200) to make API calls</li>
 * </ul>
 * 
 * <h2>Public Endpoints:</h2>
 * The following endpoints are accessible without authentication:
 * <ul>
 *   <li>/auth/** - Authentication endpoints</li>
 *   <li>/oauth2/** - OAuth 2.0 endpoints</li>
 *   <li>/swagger-ui/** - API documentation</li>
 *   <li>/actuator/health - Health check endpoint</li>
 * </ul>
 * 
 * @see AuthenticationFilter
 * @see PostOAuth2Authentication
 * @see OAuth2AuthenticationSuccessHandler
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private PostOAuth2Authentication postOAuth2Authentication;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    /**
     * Configures the security filter chain for HTTP requests.
     * 
     * <p>Sets up:
     * <ul>
     *   <li>CORS policy for cross-origin requests</li>
     *   <li>Disables CSRF (safe for JWT-based authentication)</li>
     *   <li>Stateless session management (no server sessions)</li>
     *   <li>Authorization rules (public vs. protected endpoints)</li>
     *   <li>OAuth 2.0 login configuration</li>
     *   <li>JWT authentication filter</li>
     * </ul>
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF protection is disabled because we use stateless JWT authentication.
                // JWT tokens in Authorization headers are not vulnerable to CSRF attacks
                // since they are not automatically sent by the browser like cookies.
                // If using cookie-based auth in production, re-enable CSRF protection.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/", "/error", "/favicon.ico", "/auth/**", "/oauth2/**", "/login", "/logout").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(postOAuth2Authentication)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * 
     * <p>Allows the Angular frontend running on localhost:4200 to make API calls.
     * In production, update allowed origins to match your deployed frontend URL.
     * 
     * <h3>Configuration:</h3>
     * <ul>
     *   <li><b>Allowed Origins:</b> http://localhost:4200, http://localhost:3000</li>
     *   <li><b>Allowed Methods:</b> GET, POST, PUT, DELETE, PATCH, OPTIONS</li>
     *   <li><b>Allowed Headers:</b> All headers (*)</li>
     *   <li><b>Allow Credentials:</b> true (for cookies/auth headers)</li>
     *   <li><b>Max Age:</b> 3600 seconds (1 hour) for preflight caching</li>
     * </ul>
     * 
     * @return the configured CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
