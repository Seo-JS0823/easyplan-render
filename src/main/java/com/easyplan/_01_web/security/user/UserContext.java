package com.easyplan._01_web.security.user;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.easyplan._03_domain.user.model.User;

import lombok.Getter;

@Getter
public class UserContext {
	private final Long id;
	
	private final String publicId;
	
	private final List<SimpleGrantedAuthority> role;
	
	private final boolean deleted;
	
	private final String nickname;
	
	public UserContext(User user)  {
		this.id = user.getId();
		this.nickname = user.getNickname().getValue();
		this.publicId = user.getPublicId().getValue();
		this.deleted = user.getDeletedAt() == null ? false : true;
		this.role = user.getRole().getRole().stream()
				.map(SimpleGrantedAuthority :: new)
				.toList();
	}
}
