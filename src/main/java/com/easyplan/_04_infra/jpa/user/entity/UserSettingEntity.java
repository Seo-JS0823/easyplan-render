package com.easyplan._04_infra.jpa.user.entity;

import java.time.Instant;

import com.easyplan._03_domain.user.model.UserSetting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserSettingEntity {
	@Id
	@Column(name = "user_id")
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_setting"))
	private UserEntity user;
	
	@Column(name = "notification_allowed", nullable = false)
	private boolean notificationAllowed;
	
	@Column(name = "email_notification_allowed", nullable = false)
	private boolean emailNotificationAllowed;
	
	@Column(name = "search_allowed", nullable = false)
	private boolean searchAllowed;
	
	@Column(name = "friend_request_allowed", nullable = false)
	private boolean friendRequestAllowed;
	
	@Column(name = "profile_public_allowed", nullable = false)
	private boolean profilePublicAllowed;
	
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	public static UserSettingEntity from(UserSetting userSetting) {
		return UserSettingEntity.builder()
				.notificationAllowed(userSetting.isNotificationAllowed())
				.emailNotificationAllowed(userSetting.isEmailNotificationAllowed())
				.searchAllowed(userSetting.isSearchAllowed())
				.friendRequestAllowed(userSetting.isFriendRequestAllowed())
				.profilePublicAllowed(userSetting.isProfilePublicAllowed())
				.updatedAt(userSetting.getUpdatedAt())
				.build();
	}
	
	public UserSetting toDomain() {
		return UserSetting.builder()
				.notificationAllowed(notificationAllowed)
				.emailNotificationAllowed(emailNotificationAllowed)
				.searchAllowed(searchAllowed)
				.friendRequestAllowed(friendRequestAllowed)
				.profilePublicAllowed(profilePublicAllowed)
				.updatedAt(updatedAt)
				.build();
	}
	
	public void setUser(UserEntity user) {
		this.user = user;
	}

	public void update(UserSetting userSetting) {
		this.notificationAllowed = userSetting.isNotificationAllowed();
		this.emailNotificationAllowed = userSetting.isEmailNotificationAllowed();
		this.searchAllowed = userSetting.isSearchAllowed();
		this.friendRequestAllowed = userSetting.isFriendRequestAllowed();
		this.profilePublicAllowed = userSetting.isProfilePublicAllowed();
	}
}
