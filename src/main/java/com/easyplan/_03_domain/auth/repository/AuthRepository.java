package com.easyplan._03_domain.auth.repository;

import java.util.Optional;

import com.easyplan._03_domain.auth.model.AuthSession;

public interface AuthRepository {
	AuthSession saveAuth(AuthSession auth);
	
	Optional<AuthSession> findByPublicId(String publicId);
	
	Optional<AuthSession> findByTokenHash(String tokenHash);
}
