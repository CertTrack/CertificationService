package com.certTrack.CertificationService.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JWTAuthenticationFilter filter;
	
	@Bean
	public SecurityFilterChain applicationsecurity(HttpSecurity http) throws Exception{
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		http
			.cors(CorsConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagement->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(formLogin->formLogin.disable())
			.securityMatcher("/certifications/**")
			.authorizeHttpRequests(
					(registry) -> registry
						.requestMatchers("admin/delete").hasRole("ADMIN")
						.anyRequest().authenticated()
					);
		return http.build();
	}
	
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}