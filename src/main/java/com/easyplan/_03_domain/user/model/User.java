package com.easyplan._03_domain.user.model;

import java.time.Instant;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class User {
	private Long id;
	
	private final Email email;
	
	private PasswordHash passwordHash;
	
	private Nickname nickname;
	
	private final PublicId publicId;
	
	private UserStatus status;
	
	private Role role;
	
	private UserProfile userProfile;
	
	private UserSetting userSetting;
	
	private Instant lastLoginAt;
	
	private final Instant createdAt;
	
	private Instant updatedAt;
	
	private Instant deletedAt;
	
	private boolean emailVerified;
	
	public static User create(Email email, PasswordHash passwordHash, Nickname nickname, Instant now, boolean notification, boolean emailNotification) {
		return User.builder()
				.id(null)
				.email(email)
				.passwordHash(passwordHash)
				.nickname(nickname)
				.publicId(PublicId.create())
				.status(UserStatus.ACTIVE)
				.role(Role.USER)
				.createdAt(now)
				.updatedAt(now)
				.deletedAt(null)
				.lastLoginAt(null)
				.emailVerified(false)
				.userProfile(UserProfile.createDefault())
				.userSetting(UserSetting.createDefault(notification, emailNotification))
				.build();
	}
	
	public void emailVerifySuccess() {
		this.emailVerified = true;
	}
	
	public void updateLastLoginAt(Instant now) {
		this.lastLoginAt = now;
	}
	
	public void updatePasswordHash(PasswordHash passwordHash, Instant now) {
		ensureActive();
		this.passwordHash = passwordHash;
		onUpdate(now);
	}
	
	public void updateNickname(Nickname nickname, Instant now) {
		ensureActive();
		this.nickname = nickname;
		onUpdate(now);
	}
	
	public void updateRole(Role role, Instant now) {
		ensureActive();
		this.role = role;
		onUpdate(now);
	}
	
	public void updateUserProfile(UserProfile userProfile) {
		ensureActive();
		this.userProfile = userProfile;
		onUpdate(userProfile.getUpdatedAt());
	}
	
	public void updateUserSetting(UserSetting userSetting) {
		ensureActive();
		this.userSetting = userSetting;
		onUpdate(userSetting.getUpdatedAt());
	}
	
	public void userDelete(Instant now) {
		this.status = UserStatus.DELETED;
		onUpdate(now);
	}
	
	public void userActivate(Instant now) {
		this.status = UserStatus.ACTIVE;
		onUpdate(now);
	}
	
	private void onUpdate(Instant now) {
		this.updatedAt = now;
	}
	
	/* GUARD */
	private void ensureActive() {
		if(this.status == UserStatus.DELETED) {
			throw new UserException(UserError.CANNOT_MODIFY_DELETED_USER);
		}
	}
}