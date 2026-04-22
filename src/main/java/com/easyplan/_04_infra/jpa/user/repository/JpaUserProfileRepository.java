package com.easyplan._04_infra.jpa.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.user.entity.UserProfileEntity;

public interface JpaUserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

}
