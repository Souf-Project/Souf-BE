package com.souf.soufwebsite.global.config;

import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.report.service.BanService;
import com.souf.soufwebsite.global.jwt.BanCheckFilter;
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

    private final BanService banService;

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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new BanCheckFilter(banService), JwtAuthenticationFilter.class);

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

                                .requestMatchers("/v1/normal/check").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/social/**").permitAll()
                                .requestMatchers("/api/v1/recruit/popular", "/api/v1/feed/popular", "/api/v1/member",
                                        "/api/v1/recruit/search").permitAll()

                                // 1) 인증 필요한 특정 GET (더 구체적인 경로를 먼저!)
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/recruit/my",
                                        "/api/v1/member/**",
                                        "/api/v1/notifications/**"
                                ).authenticated()

                                // 2) 공개 GET (그 다음에 포괄적인 공개 GET)
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/feed/**",
                                        "api/v1/feed/*/*",
                                        "/api/v1/recruit/**",
                                        "/api/v1/view/**",
                                        "/api/v1/post/**",
                                        "api/v1/review/**",
                                        "/api/v1/search" // 주의: member/**는 위에서 authenticated 처리
                                ).permitAll()

                                // 3) STUDENT 전용
                                .requestMatchers(HttpMethod.POST,   "/api/v1/applications/*/apply").hasRole("STUDENT")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/applications/*/apply").hasRole("STUDENT")
                                .requestMatchers(HttpMethod.GET,    "/api/v1/applications/my").hasRole("STUDENT")

                                // 4) MEMBER/ADMIN 전용
                                .requestMatchers(HttpMethod.GET,  "/api/v1/applications/*/applicants").hasAnyRole("MEMBER","ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/applications/*/approve", "/api/v1/applications/*/reject")
                                .hasAnyRole("MEMBER","ADMIN")

                                // 5) 쓰기 권한(POST/PUT/PATCH/DELETE) — 리소스별로 묶어서
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/feed/*/like").hasAnyRole("MEMBER","ADMIN","STUDENT")
                                // recruit: MEMBER, ADMIN만 쓰기 허용
                                .requestMatchers(HttpMethod.POST,   "/api/v1/recruit/**").hasAnyRole("MEMBER","ADMIN")
                                .requestMatchers(HttpMethod.PUT,    "/api/v1/recruit/**").hasAnyRole("MEMBER","ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/recruit/**").hasAnyRole("MEMBER","ADMIN")

                                // feed: STUDENT만(또는 운영 포함하려면 STUDENT, ADMIN)
                                .requestMatchers(HttpMethod.POST,   "/api/v1/feed/**").hasAnyRole("STUDENT","ADMIN")
                                .requestMatchers(HttpMethod.PUT,    "/api/v1/feed/**").hasAnyRole("STUDENT","ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/feed/**").hasAnyRole("STUDENT","ADMIN")

                                // post: 기존 정책에 맞게 별도로
                                .requestMatchers(HttpMethod.POST,   "/api/v1/post/**").hasAnyRole("MEMBER","ADMIN","STUDENT")
                                .requestMatchers(HttpMethod.PUT,    "/api/v1/post/**").hasAnyRole("MEMBER","ADMIN","STUDENT")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/post/**").hasAnyRole("MEMBER","ADMIN","STUDENT")

                                // 6) 관리자 전용
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                                // 7) 나머지
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
