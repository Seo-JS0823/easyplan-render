package com.easyplan._01_web.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CUserDetailsService implements UserDetailsService {

	private final UserRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PublicId publicId = PublicId.of(username);
		
		User user = repo.findByPublicId(publicId)
				.orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException"));
		
		return new CUserDetails(user);
	}

}
