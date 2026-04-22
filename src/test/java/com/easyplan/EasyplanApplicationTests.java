package com.easyplan;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.PasswordHash;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserRepository;
import com.easyplan._03_domain.user.service.PasswordService;
import com.easyplan.shared.time.Clock;

@SpringBootTest
class EasyplanApplicationTests {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordService passwordService;
	
	@Autowired
	Clock clock;
	
	@Test
	void contextLoads() {
		Email email = Email.of("admin@easyplan.com");
		Nickname nickname = Nickname.of("ADMIN");
		Instant now = clock.now();
		Password password = Password.of("password01@");
		PasswordHash passwordHash = passwordService.encode(password);
		
		User user = User.create(email, passwordHash, nickname, now, false, false);
		userRepo.userSave(user);
	}

}
