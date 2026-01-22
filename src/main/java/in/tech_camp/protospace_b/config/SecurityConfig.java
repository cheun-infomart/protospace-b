package in.tech_camp.protospace_b.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private final CustomLoginSession customLoginSession;

        public SecurityConfig(CustomLoginSession customLoginSession) {
                this.customLoginSession = customLoginSession;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/",
                                                                "/users/register", "/users/login",
                                                                "/.well-known/**",
                                                                "/favicon.ico",
                                                                "/prototypes/{id:[0-9]+}", "/users/{id:[0-9]+}",
                                                                "/prototypes/search/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/user").permitAll()
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginProcessingUrl("/login")
                                                .loginPage("/users/login")
                                                .failureUrl("/login?error")
                                                .successHandler(customLoginSession)
                                                .usernameParameter("email")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/"));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
