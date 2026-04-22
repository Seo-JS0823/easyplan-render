package com.easyplan._04_infra.jpa.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.user.entity.UserEntity;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
	boolean existsByEmail(String email);
	
	boolean existsByNickname(String nickname);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByPublicId(String publicId);
}
