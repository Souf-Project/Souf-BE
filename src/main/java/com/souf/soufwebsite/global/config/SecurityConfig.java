package com.souf.soufwebsite.global.config;

import com.souf.soufwebsite.global.jwt.JwtAuthenticationProcessingFilter;
import com.souf.soufwebsite.global.jwt.JwtLogoutHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

//    스프링 시큐리티 기능 비활성화 (H2 DB 접근을 위해)
//	@Bean
//	public WebSecurityCustomizer configure() {
//		return (web -> web.ignoring()
//				.requestMatchers(toH2Console())
//				.requestMatchers("/h2-console/**")
//		);
//	}
    private final JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;
    private final JwtLogoutHandler jwtLogoutHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    .csrf(AbstractHttpConfigurer::disable)
                .cors()
                .and()
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                        //.requestMatchers("/login").permitAll() // 로그인한 유저에게만 서비스 제공
                        //.anyRequest().authenticated())

                .logout((logout) -> logout
                        .addLogoutHandler(jwtLogoutHandler) // JwtLogoutHandler 추가
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterBefore(jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
