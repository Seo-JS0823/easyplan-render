package com.easyplan._03_domain.user.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class UserSetting {
	private boolean notificationAllowed;
	
	private boolean emailNotificationAllowed;
	
	private boolean searchAllowed;
	
	private boolean friendRequestAllowed;
	
	private boolean profilePublicAllowed;
	
	private Instant updatedAt;
	
	public static UserSetting createDefault(boolean notificationAllowed, boolean emailNotificationAllowd) {
		return UserSetting.builder()
				.notificationAllowed(notificationAllowed)
				.emailNotificationAllowed(emailNotificationAllowd)
				.searchAllowed(true)
				.friendRequestAllowed(true)
				.profilePublicAllowed(true)
				.build();
	}
	
	public void updateNotificationAllowed(boolean newNotification, Instant now) {
		this.notificationAllowed = newNotification;
	}
	
	public void updateEmailNotificationAllowed(boolean newEmailNotification, Instant now) {
		this.emailNotificationAllowed = newEmailNotification;
	}
	
	public void updateSearchAllowed(boolean newSearch, Instant now) {
		this.searchAllowed = newSearch;
	}
	
	public void updateFriendRequestAllowed(boolean newFriendRequest, Instant now) {
		this.friendRequestAllowed = newFriendRequest;
	}
	
	public void updateProfilePublicAllowed(boolean newProfilePublic, Instant now) {
		this.profilePublicAllowed = newProfilePublic;
	}
	
	public void onUpdate(Instant now) {
		this.updatedAt = now;
	}
}