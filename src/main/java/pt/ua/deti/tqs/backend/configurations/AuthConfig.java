package pt.ua.deti.tqs.backend.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pt.ua.deti.tqs.backend.components.AuthEntryPointJwt;
import pt.ua.deti.tqs.backend.components.AuthTokenFilter;
import pt.ua.deti.tqs.backend.services.UserService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AuthConfig {
    private UserService userService;
    private AuthTokenFilter authenticationJwtTokenFilter;
    private AuthEntryPointJwt unauthorizedHandler;
    private PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                    auth -> auth.requestMatchers(HttpMethod.POST, "/api/public/user/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/backoffice/user/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/public/bus/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/public/city/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/public/trip/**").permitAll()

                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/docs/**").permitAll()
                                .requestMatchers("/api/docs-config/**").permitAll()

                                .requestMatchers("/api/public/**").hasRole("USER")
                                .requestMatchers("/api/backoffice/**").hasAnyRole("STAFF", "ADMIN")
                                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .bearerFormat("JWT")
                                   .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                            .components(new Components().addSecuritySchemes
                                                                ("Bearer Authentication", createAPIKeyScheme()))
                            .info(new Info().title("CityConnect API")
                                            .description("Every API endpoint used in the app.")
                                            .version("1.0"));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://staff.localhost");
        configuration.addAllowedOrigin("http://digital.localhost");
        configuration.addAllowedOrigin("http://localhost");
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(
                Arrays.asList("X-Requested-With", "Cache-Control", "Cookie", "Origin", "Content-Type", "Accept",
                              "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}