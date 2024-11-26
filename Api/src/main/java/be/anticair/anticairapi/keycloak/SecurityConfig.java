package be.anticair.anticairapi.keycloak;

import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

/*** Needs to be done further
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/api/groups/add", "/api/groups/remove")
                .access("@securityConfig.hasAdminRole()") // Use method security to check admin role
                .anyRequest().authenticated()
                .and();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new KeycloakAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }

    public boolean hasAdminRole() {
        try {
            KeycloakAuthenticationToken authenticationToken = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            if (authenticationToken != null) {
                // Get the roles or groups from Keycloak (this will be in the form of granted authorities)
                Collection<? extends GrantedAuthority> authorities = authenticationToken.getAuthorities();

                // Log the authorities for debugging
                System.out.println(authenticationToken.getName());
                System.out.println(authorities);

                // Check if user has "ROLE_ADMIN" in the list of granted authorities
                return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            }
        } catch (Exception e) {
            // Log error with the exception
            System.out.println("Error checking admin role");
        }
        return false;
    }
}
**/