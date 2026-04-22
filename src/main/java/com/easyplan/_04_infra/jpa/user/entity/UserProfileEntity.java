package com.easyplan._04_infra.jpa.user.entity;

import java.time.Instant;

import com.easyplan._03_domain.user.model.Birthdate;
import com.easyplan._03_domain.user.model.UserProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserProfileEntity {
	@Id
	@Column(name = "user_id")
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_profile"))
	private UserEntity user;
	
	@Column(name = "profile_image_url", length = 500)
	private String profileImageUrl;
	
	@Column(name = "intro", length = 500)
	private String intro;
	
	@Column(name = "birthdate")
	private String birthdate;
	
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	public static UserProfileEntity from(UserProfile userProfile) {
		return UserProfileEntity.builder()
				.profileImageUrl(userProfile.getProfileImageUrl())
				.intro(userProfile.getIntro())
				.birthdate(userProfile.getBirthdate() != null ? userProfile.getBirthdate().getValue() : null)
				.updatedAt(userProfile.getUpdatedAt())
				.build();
	}
	
	public UserProfile toDomain() {
		return UserProfile.builder()
				.profileImageUrl(profileImageUrl)
				.intro(intro)
				.birthdate(Birthdate.of(birthdate))
				.updatedAt(updatedAt)
				.build();
	}
	
	public void setUser(UserEntity user) {
		this.user = user;
	}
	
	public void update(UserProfile userProfile) {
		this.profileImageUrl = userProfile.getProfileImageUrl();
		this.intro = userProfile.getIntro();
		this.birthdate = userProfile.getBirthdate() != null ? userProfile.getBirthdate().getValue() : null;
		this.updatedAt = userProfile.getUpdatedAt();
	}
}
