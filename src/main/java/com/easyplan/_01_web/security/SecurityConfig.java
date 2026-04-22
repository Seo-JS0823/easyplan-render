package com.easyplan._01_web.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.easyplan._01_web.security.filter.CsrfCookieFilter;
import com.easyplan._01_web.security.filter.JwtFilter;
import com.easyplan._01_web.webutil.CookieProvider;
import com.easyplan._03_domain.auth.service.TokenService;
import com.easyplan._03_domain.user.model.Role;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserDetailsService userDetailsService;
	
	private final TokenService tokenService;
	
	private final CookieProvider cookieProvider;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
		csrfRequestHandler.setCsrfRequestAttributeName(null);
		
		http
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		
		.formLogin(AbstractHttpConfigurer::disable)
		
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		
		.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(csrfRequestHandler))
		
		.httpBasic(AbstractHttpConfigurer::disable)
		
		.addFilterAfter(csrfCookieFilter(), BasicAuthenticationFilter.class)
		
		.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
		
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/user/verify-success", "/user/verify-fail", "/user/verify").permitAll()
				.requestMatchers("/api/user/join", "/api/user/login").permitAll()
				.requestMatchers("/user/my").hasAnyAuthority(Role.USER.getRole().toArray(new String[0]))
				.anyRequest().authenticated()
		)
		;
		
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("https://easyplan.onrender.com:8080");
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("Set-Cookie"));
		config.setMaxAge(3600L);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		return source;
	}
	
	@Bean
	JwtFilter jwtFilter() {
		return new JwtFilter(userDetailsService, cookieProvider, tokenService);
	}
	
	@Bean
	CsrfCookieFilter csrfCookieFilter() {
		return new CsrfCookieFilter();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/.well-known/**");
	}
}
