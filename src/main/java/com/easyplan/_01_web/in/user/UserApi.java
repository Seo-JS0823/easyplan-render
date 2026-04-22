package com.easyplan._01_web.in.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._01_web.GlobalResponse;
import com.easyplan._01_web.in.user.request.UserJoinRequest;
import com.easyplan._01_web.in.user.request.UserLoginRequest;
import com.easyplan._01_web.in.user.request.UserUpdateRequest;
import com.easyplan._01_web.webutil.CookieProp;
import com.easyplan._01_web.webutil.CookieProvider;
import com.easyplan._02_app.UserApplication;
import com.easyplan._02_app.command.UserCommand;
import com.easyplan._03_domain.auth.model.TokenPair;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.repository.UserUpdateCommand;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApi {
	private final UserApplication userApp;
	
	private final CookieProvider cookieProv;
	
	@PostMapping("/join")
	public ResponseEntity<?> join(@RequestBody UserJoinRequest userJoin) {
		
		System.out.println(userJoin);
		UserCommand.Join join = userJoin.toCommand();
		
		User user = userApp.userJoin(join);
		
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest userLogin, HttpServletRequest request, HttpServletResponse response) {
		UserCommand.Login login = userLogin.toCommand();
		
		TokenPair token = userApp.userLogin(login);
		
		cookieProv.addCookie(CookieProp.ACCESS, token.getAccessToken(), response);
		cookieProv.addCookie(CookieProp.REFRESH, token.getRefreshToken(), response);
		
		String zoneId = request.getHeader("Time-Zone");
		cookieProv.addCookie(CookieProp.ZONE_ID, zoneId, response);
		
		return ResponseEntity.ok(token);
	}
	
	@PostMapping("/match")
	public ResponseEntity<?> passwordMatch(@RequestBody UserUpdateRequest userUpdate, Authentication auth) {
		userApp.passwordMatch(auth.getName(), userUpdate.getCurrentPassword());
		
		return ResponseEntity.ok(GlobalResponse.success(""));
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> update(@RequestBody UserUpdateRequest userUpdate, HttpServletResponse response, Authentication auth) {
		UserUpdateCommand updateCommand = userUpdate.toCommand(auth.getName());
		
		User user = userApp.userUpdate(updateCommand);
		
		return ResponseEntity.ok(user);
	}
}
