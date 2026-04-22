package com.easyplan._03_domain.user.service;

import java.time.Instant;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.PasswordHash;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserRepository;
import com.easyplan._03_domain.user.repository.UserUpdate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
	private final UserRepository repo;
	
	private final PasswordService passwordService;
	
	public void duplicateEmail(Email email) {
		if(repo.emailDuplicateCheck(email)) {
			throw new UserException(UserError.DUPLICATE_EMAIL);
		}
	}
	
	public void duplicateNickname(Nickname nickname) {
		if(repo.nicknameDuplicateCheck(nickname)) {
			throw new UserException(UserError.DUPLICATE_NICKNAME);			
		}
	}
	
	public User createUser(Email email, Nickname nickname, Password password, boolean notificaition, boolean emailNotification, Instant now) {
		if(repo.emailDuplicateCheck(email)) {
			throw new UserException(UserError.DUPLICATE_EMAIL);
		}
		
		if(repo.nicknameDuplicateCheck(nickname)) {
			throw new UserException(UserError.DUPLICATE_NICKNAME);			
		}
		
		PasswordHash passwordHash = passwordService.encode(password);
		
		User user = User.create(email, passwordHash, nickname, now, notificaition, emailNotification);
		User saved = repo.userSave(user);
		
		return saved;
	}
	
	public User emailPasswordMatch(Email email, Password password) {
		User user = repo.findByEmail(email)
				.orElseThrow(() -> new UserException(UserError.LOGIN_NOT_MATCH));
		
		PasswordHash userPasswordHash = user.getPasswordHash();
		
		if(!passwordService.matches(password, userPasswordHash)) {
			throw new UserException(UserError.LOGIN_NOT_MATCH);
		}
		
		return user;
	}
	
	public boolean emailVerify(Email email) {
		User user = repo.findByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
		user.emailVerifySuccess();
		
		repo.apply(user, UserUpdate.EMAIL_VERIFIED);
		
		User updated = repo.findByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
		
		if(updated.isEmailVerified() == true) {
			return true;
		} else {
			return false;
		}
	}
	
	public PasswordHash encode(Password password) {
		return passwordService.encode(password);
	}
	
	/* 
	 * UserService에 Update 메서드가 바뀔 수 있는 Column마다 하나씩 추가되는 것이 싫고
	 * 불필요하게 Service가 비대해지는 느낌이어서 Application Layer의 UserCommand에 Interface로
	 * 상세적으로 구현시켜 놓았음.
	 * 
	 * 2026-04-14: 결국에 UserService가 비대해지는 것을 막았지만 도메인 모델에서 관리되어야 할 것 같은
	 * 행위와 그 상세 구현이 외부 Application Layer로 유출되어서 도메인 모델이 쪼개진 듯 한 느낌.
	 * 이 프로젝트를 목표했던 것 까지 완성시키고, 리팩토링 버전을 만들어서 비교해봅세.
	 */
	public void userUpdate(User user, UserUpdate update) {
		repo.apply(user, update);
	}

	public User findByPublicId(PublicId publicId) {
		return repo.findByPublicId(publicId)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
	}

	public User findByEmail(Email email) {
		return repo.findByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
	}
}
