package com.easyplan._04_infra.jpa.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.auth.entity.AuthEntity;

public interface JpaAuthRepository extends JpaRepository<AuthEntity, Long> {
	Optional<AuthEntity> findByPublicId(String publicId);
	
	Optional<AuthEntity> findByTokenHash(String tokenHash);
	
	
}