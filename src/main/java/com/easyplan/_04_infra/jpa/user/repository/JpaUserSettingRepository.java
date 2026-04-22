package com.easyplan._04_infra.jpa.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.user.entity.UserSettingEntity;

public interface JpaUserSettingRepository extends JpaRepository<UserSettingEntity, Long> {

}
