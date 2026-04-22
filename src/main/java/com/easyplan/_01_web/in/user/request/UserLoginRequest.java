package com.easyplan._01_web.in.user.request;

import com.easyplan._02_app.command.UserCommand;
import com.easyplan._02_app.command.UserCommand.Login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginRequest {
	private String email;
	
	private String password;
	
	public UserCommand.Login toCommand() {
		return new Login(email, password);
	}
}
