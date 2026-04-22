package com.easyplan._04_infra.jpa.user.repository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.PasswordHash;
import com.easyplan._03_domain.user.service.PasswordService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

	private final PasswordEncoder passwordEncoder;
	
	@Override
	public PasswordHash encode(Password password) {
		String encoded = passwordEncoder.encode(password.getValue());
		
		return PasswordHash.of(encoded);
	}

	@Override
	public boolean matches(Password rawPassword, PasswordHash encodedPassword) {
		String raw = rawPassword.getValue();
		String encoded = encodedPassword.getValue();
		
		return passwordEncoder.matches(raw, encoded);
	}
	
}
