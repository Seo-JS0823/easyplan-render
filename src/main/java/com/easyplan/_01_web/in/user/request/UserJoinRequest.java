package com.easyplan._01_web.in.user.request;

import com.easyplan._02_app.command.UserCommand;
import com.easyplan._02_app.command.UserCommand.Join;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserJoinRequest {
	private String email;
	
	private String password;
	
	private String nickname;
	
	private boolean notification;
	
	private boolean emailNotification;
	
	public UserCommand.Join toCommand() {
		return new Join(email, password, nickname, notification, emailNotification);
	}
}
