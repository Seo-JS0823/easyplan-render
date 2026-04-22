package com.easyplan._03_domain.user.repository;

import java.util.Optional;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.User;

public interface UserRepository {
	User userSave(User user);
	
	boolean emailDuplicateCheck(Email email);
	
	boolean nicknameDuplicateCheck(Nickname nickname);
	
	Optional<User> findByEmail(Email email);
	
	Optional<User> findByPublicId(PublicId publicId);
	
	void apply(User user, UserUpdate update);
}
