package com.nad.start_spring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { "/users",
            "/auth/token", "/auth/introspect", "auth/logout", "/auth/refresh", "/auth/forgot-password",
            "/auth/reset-password",
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                }) // bật CORS, không cần .and()
                .csrf(AbstractHttpConfigurer::disable)
                // .authorizeHttpRequests(auth -> auth
                // .anyRequest().permitAll()
                // );
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // Register
                        .requestMatchers("/users/myInfo").authenticated() // Login user info
                        .requestMatchers("/users/**").hasAuthority("ADMIN") // Other user operations

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/books/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/category/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/category/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/category/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/loans/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/loans/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/loans/**").hasAuthority("ADMIN")
                        .requestMatchers("/loan-details/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/fine/**").authenticated() // Allow any authenticated user to
                                                                                     // view fines
                        .requestMatchers(HttpMethod.POST, "/fine/**").hasAuthority("ADMIN") // Only ADMIN
                        .requestMatchers(HttpMethod.PUT, "/fine/**").hasAuthority("ADMIN") // Only ADMIN

                        .requestMatchers("/payment/**").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/favorites/**").authenticated() // User must be logged in
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntry()));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}