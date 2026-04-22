package com.easyplan._04_infra.jpa.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserRepository;
import com.easyplan._03_domain.user.repository.UserUpdate;
import com.easyplan._04_infra.jpa.user.entity.UserEntity;
import com.easyplan._04_infra.jpa.user.entity.UserProfileEntity;
import com.easyplan._04_infra.jpa.user.entity.UserSettingEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
	private final JpaUserRepository userRepo;
	
	private final JpaUserSettingRepository userSettingRepo;
	
	private final JpaUserProfileRepository userProfileRepo;

	@Override
	public User userSave(User user) {
		UserEntity userEntity = UserEntity.from(user);
		UserProfileEntity userProfileEntity = UserProfileEntity.from(user.getUserProfile());
		UserSettingEntity userSettingEntity = UserSettingEntity.from(user.getUserSetting());
		
		userEntity.setUserProfile(userProfileEntity);
		userEntity.setUserSetting(userSettingEntity);
		
		UserEntity saved = userRepo.save(userEntity);
		return saved.toDomain();
	}

	@Override
	public boolean emailDuplicateCheck(Email email) {
		return userRepo.existsByEmail(email.getValue());
	}

	@Override
	public boolean nicknameDuplicateCheck(Nickname nickname) {
		return userRepo.existsByNickname(nickname.getValue());
	}

	@Override
	public Optional<User> findByEmail(Email email) {
		return userRepo.findByEmail(email.getValue())
				.map(UserEntity :: toDomain);
	}

	@Override
	public Optional<User> findByPublicId(PublicId publicId) {
		return userRepo.findByPublicId(publicId.getValue())
				.map(UserEntity :: toDomain);
	}
	
	@Override
	public void apply(User user, UserUpdate update) {
		UserEntity selected = userRepo.findById(user.getId())
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
		
		switch (update) {
		case EMAIL_VERIFIED -> selected.emailVerifySuccess(user);
		case LAST_LOGIN_AT -> selected.updateLastLoginAt(user);
		case DELETED_AT -> selected.updateStatus(user);
		case NICKNAME -> selected.updateNickname(user);
		case PASSWORD_HASH -> selected.updatePasswordHash(user);
		case USER_PROFILE -> selected.updateProfile(user);
		case USER_SETTING -> selected.updateSetting(user);
		case STATUS -> selected.updateStatus(user);
		case ROLE -> selected.updateRole(user);
		default -> throw new UserException(UserError.NOT_UPDATED_LIST);
		}
	}
	
}
