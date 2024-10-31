package com.example.spring_login.oauth.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import com.example.spring_login.login.domain.Constants;
import com.example.spring_login.oauth.security.filter.JwtAuthenticationFilter;
import com.example.spring_login.oauth.security.filter.CustomJwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

	private static final String[] PERMIT_ALL_PATTERNS = {
		"/api/**",
		"/api/oauth",
		"/api/oauth/refresh-kakao-token",
		"/api/oauth/logout",
		"/api/oauth/callback",
		"/api/oauth/refresh",
		"/actuator/health",
		"/api-docs.html",
		"/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html"
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagementConfigurer ->
				sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exceptionHandlingConfigurer ->
				exceptionHandlingConfigurer.authenticationEntryPoint(customJwtAuthenticationEntryPoint))
			.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry
					.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
					.requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
					.anyRequest().authenticated())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList(
			"http://localhost:8080",
			"http://localhost:63342",
			"http://localhost:4200",
			"http://20.39.188.226:8080",
			"https://kauth.kakao.com"
		));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}