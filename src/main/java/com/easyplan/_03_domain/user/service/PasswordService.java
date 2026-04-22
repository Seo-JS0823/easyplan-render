package com.easyplan._03_domain.user.service;

import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.PasswordHash;

public interface PasswordService {
	PasswordHash encode(Password password);
	
	boolean matches(Password rawPassword, PasswordHash encodedPassword);
}
