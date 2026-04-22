package com.easyplan._04_infra.jpa.user.entity;

import java.time.Instant;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.PasswordHash;
import com.easyplan._03_domain.user.model.Role;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.model.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@ToString
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "email", unique = true, nullable = false, updatable = false)
	private String email;
	
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;
	
	@Column(name = "nickname", unique = true, nullable = false, length = 10)
	private String nickname;
	
	@Column(name = "public_id", unique = true, nullable = false, length= 36)
	private String publicId;
	
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	
	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Column(name = "last_login_at")
	private Instant lastLoginAt;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	@Column(name = "deleted_at")
	private Instant deletedAt;
	
	@Column(name = "email_verified", nullable = false)
	private boolean emailVerified;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private UserProfileEntity userProfile;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private UserSettingEntity userSetting;
	
	public static UserEntity from(User user) {
		return UserEntity.builder()
				.id(user.getId())
				.email(user.getEmail().getValue())
				.passwordHash(user.getPasswordHash().getValue())
				.nickname(user.getNickname().getValue())
				.publicId(user.getPublicId().getValue())
				.status(user.getStatus())
				.role(user.getRole())
				.lastLoginAt(user.getLastLoginAt())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.deletedAt(user.getDeletedAt())
				.emailVerified(user.isEmailVerified())
				.build();
	}
	
	public User toDomain() {
		return User.builder()
				.id(id)
				.email(Email.of(email))
				.passwordHash(PasswordHash.of(passwordHash))
				.nickname(Nickname.of(nickname))
				.publicId(PublicId.of(publicId))
				.status(status)
				.role(role)
				.lastLoginAt(lastLoginAt)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.deletedAt(deletedAt)
				.emailVerified(emailVerified)
				.userProfile(userProfile.toDomain())
				.userSetting(userSetting.toDomain())
				.build();
	}
	
	public void setUserProfile(UserProfileEntity userProfile) {
		this.userProfile = userProfile;
		if (userProfile != null && userProfile.getUser() != this) {
			userProfile.setUser(this);
		}
	}
	
	public void setUserSetting(UserSettingEntity userSetting) {
		this.userSetting = userSetting;
		if (userSetting != null && userSetting.getUser() != this) {
			userSetting.setUser(this);
		}
	}
	
	public void emailVerifySuccess(User user) {
		this.emailVerified = user.isEmailVerified();
	}
	
	public void updateLastLoginAt(User user) {
		this.lastLoginAt = user.getLastLoginAt();
	}
	
	public void updatePasswordHash(User user) {
		this.passwordHash = user.getPasswordHash().getValue();
		this.updatedAt = user.getUpdatedAt();
	}
	
	public void updateNickname(User user) {
		this.nickname = user.getNickname().getValue();
		this.updatedAt = user.getUpdatedAt();
	}
	
	public void updateRole(User user) {
		this.role = user.getRole();
		this.updatedAt = user.getUpdatedAt();
	}
	
	public void updateStatus(User user) {
		this.status = user.getStatus();
		this.updatedAt = user.getUpdatedAt();
	}
	
	public void updateProfile(User user) {
		this.userProfile.update(user.getUserProfile());
		this.updatedAt = user.getUpdatedAt();
	}
	
	public void updateSetting(User user) {
		this.userSetting.update(user.getUserSetting());
		this.updatedAt = user.getUpdatedAt();
	}
	
}