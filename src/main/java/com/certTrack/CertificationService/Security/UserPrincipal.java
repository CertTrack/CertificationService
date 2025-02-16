package com.certTrack.CertificationService.Security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserPrincipal implements UserDetails{

	private int userId;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;
	@JsonIgnore
	private String password;
	
	@Override
	public String getUsername() {
		return email;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
}