package com.easyplan._03_domain.user.repository;

import java.time.Instant;

import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.service.UserService;

public interface UserUpdateCommand {
	void handle(UserService userService, Instant now);
}
