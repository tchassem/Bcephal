/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.List;

import lombok.Data;

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class BcephalUserDetails /*implements UserDetails*/ {
	
	private String login;
	private String password;
	private boolean active;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	
//	private List<GrantedAuthority> authorities;
	
	
	public BcephalUserDetails() {
		
	}
	
	public BcephalUserDetails(User user, List<Right> rights) {
//		this.login = user.getLogin();
//		//this.password = user.getPassword();
//		this.active = user.isActive();
//		this.accountExpired = user.isAccountExpired();
//		this.accountLocked = user.isAccountLocked();
//		this.credentialsExpired = user.isCredentialsExpired();
//		this.authorities = Arrays.stream((Right[])rights.toArray())
//				.map(BcephalGrantedAuthority::new)
//				.collect(Collectors.toList());
	}
	
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return authorities;
//	}
//
//	@Override
//	public String getPassword() {
//		return password;
//	}
//
//	@Override
//	public String getUsername() {
//		return login;
//	}
//
//	@Override
//	public boolean isAccountNonExpired() {
//		return !accountExpired;
//	}
//
//	@Override
//	public boolean isAccountNonLocked() {
//		return !accountLocked;
//	}
//
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return !credentialsExpired;
//	}
//
//	@Override
//	public boolean isEnabled() {
//		return active;
//	}

}
