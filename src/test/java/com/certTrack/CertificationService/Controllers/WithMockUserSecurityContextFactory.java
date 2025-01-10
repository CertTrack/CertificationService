package com.certTrack.CertificationService.Controllers;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.certTrack.CertificationService.Security.UserPrincipal;
import com.certTrack.CertificationService.Security.UserPrincipalAuthenticationToken;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser>{

	@Override
	public SecurityContext createSecurityContext(WithMockUser annotation) {
		var auth = Arrays.stream(annotation.auth())
				.map(SimpleGrantedAuthority::new)
				.toList();
		var principle = UserPrincipal.builder()
				.userId(annotation.userid())
				.email("dimatrofimov515@gmail.com")
				.authorities(auth)
				.build();
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new UserPrincipalAuthenticationToken(principle));
		return context;
	} 

}
