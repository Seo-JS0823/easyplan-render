package com.easyplan._01_web.security.user;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.easyplan._03_domain.user.model.User;

public class CUserDetails implements UserDetails {
	private final UserContext user;
	
	public CUserDetails(User user) {
		this.user = new UserContext(user);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.user.getRole();
	}

	@Override
	public @Nullable String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.user.getPublicId();
	}

	@Override
	public boolean isEnabled() {
		return this.user.isDeleted();
	}
	
	public Long getUserId() {
		return user.getId();
	}
	
	public String getNickname() {
		return user.getNickname();
	}
}
