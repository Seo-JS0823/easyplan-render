package com.easyplan._02_app;

import java.time.Instant;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._02_app.command.UserCommand;
import com.easyplan._03_domain.auth.model.TokenPair;
import com.easyplan._03_domain.auth.service.AuthService;
import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserUpdateCommand;
import com.easyplan._03_domain.user.service.UserService;
import com.easyplan._04_infra.mail.EmailVerifiedService;
import com.easyplan.shared.time.Clock;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserApplication {

	private final UserService userService;
	
	private final EmailVerifiedService mailService;
	
	private final Clock clock;
	
	private final AuthService authService;
	
	@Transactional
	public User userJoin(UserCommand.Join userJoin) {
		Email email = Email.of(userJoin.email());
		Password password = Password.of(userJoin.password());
		Nickname nickname = Nickname.of(userJoin.nickname());
		boolean isNotification = userJoin.notification();
		boolean isEmailNotification = userJoin.emailNotification();
		Instant now = clock.now();
		
		User savedUser =  userService.createUser(email, nickname, password, isNotification, isEmailNotification, now);
		
		mailService.sendJoinVerification(savedUser.getEmail().getValue());
		
		return savedUser;
	}
	
	@Transactional
	public TokenPair userLogin(UserCommand.Login userLogin) {
		Email email = Email.of(userLogin.email());
		Password password = Password.loginOf(userLogin.password());
		
		User user = userService.emailPasswordMatch(email, password);
		Instant now = clock.nowSecond();
		userLogin.handle(userService, now);
		
		TokenPair tokens = authService.createTokens(user.getPublicId().getValue(), user.getRole().name(), now);
		
		authService.loginAuthUpdate(user.getPublicId().getValue(), tokens, now);
		
		return tokens;
	}
	
	@Transactional
	public boolean userEmailVerify(String token) {
		Email userEmail = Email.of(mailService.verifyToken(token));
		
		return userService.emailVerify(userEmail);
	}
	
	@Transactional
	public User userUpdate(UserUpdateCommand command) {
		Instant now = clock.nowSecond();
		command.handle(userService, now);
		
		return userService.findByPublicId(PublicId.of(SecurityContextHolder.getContext().getAuthentication().getName()));
	}
	
	@Transactional(readOnly = true)
	public User userInfo(String publicId) {
		User user = userService.findByPublicId(PublicId.of(publicId));
		return user;
	}
	
	@Transactional(readOnly = true)
	public void passwordMatch(String publicId, String password) {
		User user = userService.findByPublicId(PublicId.of(publicId));
		userService.emailPasswordMatch(user.getEmail(), Password.loginOf(password));
	}
}
