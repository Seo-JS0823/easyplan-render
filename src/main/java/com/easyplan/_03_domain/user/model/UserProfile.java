package com.easyplan._03_domain.user.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfile {
	private String profileImageUrl;
	
	private String intro;
	
	private Birthdate birthdate;
	
	private Instant updatedAt;
	
	public static UserProfile createDefault() {
		return UserProfile.builder()
				.profileImageUrl(null)
				.intro(null)
				.birthdate(null)
				.updatedAt(null)
				.build();
	}
	
	public void updateProfileImageUrl(String newProfileImageUrl, Instant now) {
		profileImageUrl = newProfileImageUrl;
	}
	
	public void updateIntro(String newIntro, Instant now) {
		intro = newIntro;
	}
	
	public void updateBirthdate(Birthdate newBirthdate, Instant now) {
		birthdate = newBirthdate;
	}
	
	public void onUpdate(Instant now) {
		this.updatedAt = now;
	}
}