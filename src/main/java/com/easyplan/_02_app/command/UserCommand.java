package com.easyplan._02_app.command;

import java.time.Instant;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.PasswordHash;
import com.easyplan._03_domain.user.model.Role;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.model.UserProfile;
import com.easyplan._03_domain.user.model.UserSetting;
import com.easyplan._03_domain.user.repository.UserUpdate;
import com.easyplan._03_domain.user.repository.UserUpdateCommand;
import com.easyplan._03_domain.user.service.UserService;

public class UserCommand {
	public record Join(String email, String password, String nickname, boolean notification, boolean emailNotification) {}
	
	public record Login(String email, String password) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByEmail(Email.of(email));
			user.updateLastLoginAt(now);
			userService.userUpdate(user, UserUpdate.LAST_LOGIN_AT);
		}
	}
	
	/* Update Record */
	public record UpdatePasswordHash(String publicId, String password, String newPassword) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			Password oldPassword = Password.of(password);
			userService.emailPasswordMatch(user.getEmail(), oldPassword);
			
			PasswordHash newPasswordHash = userService.encode(Password.of(newPassword));
			user.updatePasswordHash(newPasswordHash, now);
			userService.userUpdate(user, UserUpdate.PASSWORD_HASH);
		}
	}
	
	public record UpdateNickname(String publicId, String newNickname) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			Nickname nickname = Nickname.of(newNickname);
			userService.duplicateNickname(nickname);
			
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.updateNickname(nickname, now);
			userService.userUpdate(user, UserUpdate.NICKNAME);
		}
	}
	
	public record UpdateRole(String publicId, Role newRole) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.updateRole(newRole, now);
			userService.userUpdate(user, UserUpdate.ROLE);
		}
		
	}
	
	public record UpdateUserProfile(String publicId, UserProfile newUserProfile) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.updateUserProfile(newUserProfile);
			userService.userUpdate(user, UserUpdate.USER_PROFILE);
		}
		
	}
	
	public record UpdateUserSetting(String publicId, UserSetting newUserSetting) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.updateUserSetting(newUserSetting);
			userService.userUpdate(user, UserUpdate.USER_SETTING);
		}
		
	}
	
	public record Deleted(String publicId) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.userDelete(now);
			userService.userUpdate(user, UserUpdate.DELETED_AT);
		}
		
	}
	
	public record Activate(String publicId) implements UserUpdateCommand {

		@Override
		public void handle(UserService userService, Instant now) {
			User user = userService.findByPublicId(PublicId.of(publicId));
			user.userActivate(now);
			userService.userUpdate(user, UserUpdate.STATUS);
		}
		
	}
}
