package com.easyplan._04_infra.jpa.auth.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.auth.exception.AuthError;
import com.easyplan._03_domain.auth.exception.AuthException;
import com.easyplan._03_domain.auth.model.AuthSession;
import com.easyplan._03_domain.auth.repository.AuthRepository;
import com.easyplan._04_infra.jpa.auth.entity.AuthEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {
	private final JpaAuthRepository repo;

	@Override
	public AuthSession saveAuth(AuthSession auth) {
		Long id = auth.getId();
		
		if(id == null) {
			AuthEntity entity = AuthEntity.from(auth);
			AuthEntity saved = repo.save(entity);
			return saved.toDomain();
			
		} else {
			AuthEntity entity = repo.findById(id)
					.orElseThrow(() -> new AuthException(AuthError.EXIST_AUTH));
			
			entity.apply(auth);
			
			AuthEntity saved = repo.save(entity);
			repo.flush();
			return saved.toDomain();
		}
	}

	@Override
	public Optional<AuthSession> findByPublicId(String publicId) {
		return repo.findByPublicId(publicId)
				.map(AuthEntity :: toDomain);
	}

	@Override
	public Optional<AuthSession> findByTokenHash(String tokenHash) {
		return repo.findByTokenHash(tokenHash)
				.map(AuthEntity :: toDomain);
	}
	
	
}
