package com.ifgoiano.caixa2bank.websecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/admin/**").hasAuthority("admin")

                        .requestMatchers("/user/register").hasAuthority("user")
                        .requestMatchers("/user/login", "/user/login-error", "/error").permitAll()
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/user/update/*").hasAuthority("user")
                        .requestMatchers("/account/email-forgot-password").permitAll()
                        .requestMatchers("/account/forgot-password").permitAll()
                        .requestMatchers("/account/forgot-password/**").permitAll()
                        .requestMatchers("/webjars/**", "/style/**", "/images/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/user/verify-role-user")
                        .failureUrl("/user/login-error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                );


        return http.build();
    }
}


