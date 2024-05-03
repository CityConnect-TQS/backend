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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pt.ua.deti.tqs.backend.components.AuthEntryPointJwt;
import pt.ua.deti.tqs.backend.components.AuthTokenFilter;
import pt.ua.deti.tqs.backend.services.UserService;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AuthConfig {
    private UserService userService;
    private AuthTokenFilter authenticationJwtTokenFilter;
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                    auth -> auth.requestMatchers(HttpMethod.POST, "/api/public/user/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/public/user").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/backoffice/user").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/docs/**").permitAll()
                                .requestMatchers("/api/docs-config/**").permitAll()
                                .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .bearerFormat("JWT")
                                   .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                                                     addList("Bearer Authentication"))
                            .components(new Components().addSecuritySchemes
                                                                ("Bearer Authentication", createAPIKeyScheme()))
                            .info(new Info().title("CityConnect API")
                                            .description("Every API endpoint used in the app.")
                                            .version("1.0"));
    }
}