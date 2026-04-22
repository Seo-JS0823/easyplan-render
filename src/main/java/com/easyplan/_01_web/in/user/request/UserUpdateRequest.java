package com.easyplan._01_web.in.user.request;

import com.easyplan._02_app.command.UserCommand;
import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;
import com.easyplan._03_domain.user.model.UserProfile;
import com.easyplan._03_domain.user.model.UserSetting;
import com.easyplan._03_domain.user.repository.UserUpdate;
import com.easyplan._03_domain.user.repository.UserUpdateCommand;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserUpdateRequest {
	private UserUpdate update;
	
	private String newNickname;
	
	private String newPassword;
	
	private String currentPassword;
	
	private String newRole;
	
	private String newStatus;
	
	/* UserSetting */
	private boolean notification;
	
	private boolean emailNotification;
	
	private boolean searchAllowed;
	
	private boolean friendRequest;
	
	private boolean profilePublic;
	
	public UserUpdateCommand toCommand(String publicId) {
		return switch (update) {
		case NICKNAME -> new UserCommand.UpdateNickname(publicId, newNickname);
		case PASSWORD_HASH -> new UserCommand.UpdatePasswordHash(publicId, currentPassword, newPassword);
		case USER_PROFILE -> new UserCommand.UpdateUserSetting(
				publicId,
				UserSetting.builder()
					.searchAllowed(searchAllowed)
					.friendRequestAllowed(friendRequest)
					.profilePublicAllowed(profilePublic)
					.build());
		case USER_SETTING -> new UserCommand.UpdateUserSetting(
				publicId,
				UserSetting.builder()
					.notificationAllowed(notification)
					.emailNotificationAllowed(emailNotification)
					.build());
		default -> throw new UserException(UserError.NOT_UPDATED_LIST);
		};
	}
}
