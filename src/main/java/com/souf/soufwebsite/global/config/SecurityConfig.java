package com.souf.soufwebsite.global.config;

import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.jwt.JwtAuthenticationFilter;
import com.souf.soufwebsite.global.jwt.JwtLogoutHandler;
import com.souf.soufwebsite.global.jwt.JwtServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    private final JwtLogoutHandler jwtLogoutHandler;
    private final MemberRepository memberRepository;
    private final JwtServiceImpl jwtService;
    private final RedisTemplate<String, String> redisTemplate;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService, memberRepository, redisTemplate);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout((logout) -> logout
                        .addLogoutHandler(jwtLogoutHandler) // JwtLogoutHandler 추가
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry

                                // 공용 리소스는 누구나 접근 가능
                                .requestMatchers(
                                        "/ws/**",
                                        "/favicon.ico",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/error"
                                ).permitAll()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/feed",
                                        "/api/v1/recruit",
                                        "/api/v1/member",
                                        "/api/v1/search"
                                ).permitAll()

                                .requestMatchers("/v1/normal/check").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/recruit/popular", "/api/v1/feed/popular")
                                .permitAll()

                                // 2) STUDENT 전용: apply, withdraw, 내 지원 내역
                                .requestMatchers(HttpMethod.POST,   "/api/v1/applications/*/apply")
                                .hasRole("STUDENT")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/applications/*/apply")
                                .hasRole("STUDENT")
                                .requestMatchers(HttpMethod.GET,    "/api/v1/applications/my")
                                .hasRole("STUDENT")

                                // 3) MEMBER(=공고 작성자) 전용: 지원자 목록, 승인·거절
                                .requestMatchers(HttpMethod.GET,  "/api/v1/applications/*/applicants")
                                .hasAnyRole("MEMBER","ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/applications/*/approve",
                                        "/api/v1/applications/*/reject")
                                .hasAnyRole("MEMBER","ADMIN")

                                // 4) 기타 GET 엔드포인트: 로그인된 모든 사용자
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/feed/**",
                                        "/api/v1/recruit/**",
                                        "/api/v1/member/**"
                                ).authenticated()

                                // 5) POST/PUT/DELETE 등 기타 공고·피드 엔드포인트
                                .requestMatchers("/api/v1/recruit/**").hasAnyRole("MEMBER", "ADMIN")
                                .requestMatchers("/api/v1/feed/**").hasAnyRole("STUDENT", "ADMIN")
                                .requestMatchers("/api/v1/admin/bulk-reindex").hasAnyRole("ADMIN")
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
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

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, memberRepository, redisTemplate);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
