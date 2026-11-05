package com.apnaacademy.config;

import com.apnaacademy.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/contact", "/courses", "/courses/**", "/faculty", "/events", "/placements", "/jobs", "/notes", "/certificate/verify", "/enquiry", "/register", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/student/**", "/tests/*/take", "/tests/*/submit").hasRole("STUDENT")
                        .requestMatchers("/tests", "/tests/*").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/after-login", true).permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/?loggedout").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.migrateSession())
                        .maximumSessions(1))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(UserAccountRepository users) {
        return email -> users.findByEmailIgnoreCase(email)
                .map(account -> User.withUsername(account.getEmail())
                        .password(account.getPassword())
                        .roles(account.getRole())
                        .disabled(!Boolean.TRUE.equals(account.getEnabled()))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
