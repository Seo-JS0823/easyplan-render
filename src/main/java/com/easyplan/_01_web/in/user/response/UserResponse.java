package com.easyplan._01_web.in.user.response;

import java.time.Instant;

import com.easyplan._03_domain.user.model.User;

import lombok.Getter;

@Getter
public class UserResponse {
	private String publicId;
	
	private String email;
	
	private String nickname;
	
	private Instant createdAt;
	
	private Instant lastLoginAt;
	
	private Instant updatedAt;
	
	private String intro;
	
	private String birthdate;
	
	private boolean notification;
	
	private boolean emailNotification;
	
	private boolean searchAllowed;
	
	private boolean friendRequest;
	
	private boolean profilePublic;
	
	public UserResponse(User user) {
		this.publicId = user.getPublicId().getValue();
		this.email = user.getEmail().getValue();
		this.nickname = user.getNickname().getValue();
		this.createdAt = user.getCreatedAt();
		this.lastLoginAt = user.getLastLoginAt();
		this.updatedAt = user.getUpdatedAt();
		this.intro = user.getUserProfile().getIntro();
		this.birthdate = user.getUserProfile().getBirthdate() == null ? "미지정" : user.getUserProfile().getBirthdate().getValue();
		this.notification = user.getUserSetting().isNotificationAllowed();
		this.emailNotification = user.getUserSetting().isEmailNotificationAllowed();
		this.searchAllowed = user.getUserSetting().isSearchAllowed();
		this.friendRequest = user.getUserSetting().isFriendRequestAllowed();
		this.profilePublic = user.getUserSetting().isProfilePublicAllowed();
	}
}
