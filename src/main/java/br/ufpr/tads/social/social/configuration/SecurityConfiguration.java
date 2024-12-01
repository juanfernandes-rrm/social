package br.ufpr.tads.social.social.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//TODO: adicionar configuração para rotas publicas -> talvez deixar liberado aqui e bloquear no gateway
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {
            "/profile/*/favorites",
            "/profile/*/following",
            "/profile/*/followers",
            "/profile/*/reviews",
            "/profile/*/receipts",
            "/profile/*",
            "/comment/*",
            "/product/reviews/*",
            "/product/comments/*",
            "/ranking",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                            .requestMatchers(AUTH_WHITELIST).permitAll()
                            .anyRequest().authenticated())
                        .oauth2ResourceServer(oauth2 -> oauth2
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                        .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}
