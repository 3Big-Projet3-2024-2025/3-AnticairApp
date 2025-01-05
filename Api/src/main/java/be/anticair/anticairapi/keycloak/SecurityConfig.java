package be.anticair.anticairapi.keycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures security filter chain for HTTP requests
     * @param http HttpSecurity configuration
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     * @author Zarzycki Alexis
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuration CORS
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/listing/payment/success").permitAll()
                        .requestMatchers("/api/listing/checked").permitAll()
                        .requestMatchers("/api/photoAntiquity/images/**").permitAll()
                        .requestMatchers("/api/listing/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * Creates a JWT decoder using Keycloak's JWKS endpoint
     * @return Configured JwtDecoder
     * @author Zarzycki Alexis
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri("http://localhost:8081/realms/anticairapp/protocol/openid-connect/certs")
                .build();
    }

    /**
     * Configures CORS settings
     * @return CorsConfigurationSource
     * @author Verly Noah
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}
