package com.certTrack.CertificationService.Security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JWTDecoder decoder;
	@Autowired
	private JWTToPrincipalConverter converter;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		extractTokenFromRequest(request)
				.map(decoder::decode)
				.map(converter::convert)
				.map(UserPrincipalAuthenticationToken::new)
				.ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
		filterChain.doFilter(request, response);
	}

	private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
		var token = request.getHeader("Authorization");
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return Optional.of(token.substring(7));
		}
		log.info("filter for bearer error token: " + token);
		return Optional.empty();
	}

}