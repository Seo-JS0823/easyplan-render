package com.easyplan._01_web.in.user;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.easyplan._01_web.in.user.response.UserResponse;
import com.easyplan._01_web.webutil.CookieProp;
import com.easyplan._01_web.webutil.CookieProvider;
import com.easyplan._02_app.UserApplication;
import com.easyplan._03_domain.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserView {
	private final UserApplication userApp;
	
	private final CookieProvider cookieProv;
	
	@GetMapping("/verify-success")
	public String verifySuccessView() {
		return "/user/verify-success";
	}
	
	@GetMapping("/verify-fail")
	public String verifyFailView() {
		return "/user/verify-fail";
	}
	
	@GetMapping("/verify")
	public String joinVerify(@RequestParam String token) {
		boolean isVerify = userApp.userEmailVerify(token);
		
		if(isVerify) {
			return "redirect:/user/verify-success";
		} else {
			return "redirect:/user/verify-fail";
		}
	}
	
	@GetMapping("/my")
	public String myPageView(Model model, Authentication auth, HttpServletRequest request) {
		User user = userApp.userInfo(auth.getName());
		UserResponse userResponse = new UserResponse(user);
		
		String zoneId = cookieProv.getCookieValue(CookieProp.ZONE_ID, request);
		
		ZonedDateTime userCreatedAt = userResponse.getCreatedAt().atZone(ZoneId.of("UTC"))
				.withZoneSameInstant(ZoneId.of(zoneId));
		
		modelMetadata("user", model, userResponse);
		modelMetadata("since", model, userCreatedAt);
		return "user/mypage";
	}
	
	@GetMapping("/home")
	public String homeView(Model model, Authentication auth) {
		User user = userApp.userInfo(auth.getName());
		UserResponse userResponse = new UserResponse(user);
		modelMetadata("user", model, userResponse);
		return "user/home";
	}
	
	private void modelMetadata(String attr, Model model, Object object) {
		model.addAttribute(attr, object);
	}
}