package com.easyplan._04_infra.jpa.shared;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.account.CategoryOptionCode;
import com.easyplan._03_domain.user.model.Email;
import com.easyplan._03_domain.user.model.Nickname;
import com.easyplan._03_domain.user.model.Password;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.service.PasswordService;
import com.easyplan._04_infra.jpa.ledger.entity.CategoryOptionEntity;
import com.easyplan._04_infra.jpa.ledger.repository.JpaCategoryOptionRepository;
import com.easyplan._04_infra.jpa.user.entity.UserEntity;
import com.easyplan._04_infra.jpa.user.entity.UserProfileEntity;
import com.easyplan._04_infra.jpa.user.entity.UserSettingEntity;
import com.easyplan._04_infra.jpa.user.repository.JpaUserRepository;
import com.easyplan.shared.time.Clock;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
	private final JpaCategoryOptionRepository categoryOptionRepo;
	
	private final JpaUserRepository userRepo;
	
	private final PasswordService passwordService;
	
	private final Clock clock;
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if(categoryOptionRepo.count() == 0) {
			List<CategoryOption> defaultOptions = CategoryOptionCode.defaultOptions();
			
			List<CategoryOptionEntity> entities = defaultOptions.stream()
					.map(CategoryOptionEntity :: from)
					.toList();
			
			categoryOptionRepo.saveAll(entities);
		}
		
		if(userRepo.count() == 0) {
			User testUser = User.create(
					Email.of("admin@easyplan.com"),
					passwordService.encode(Password.of("a123456789@")),
					Nickname.of("관리자"),
					clock.nowSecond(),
					true,
					true);
			
			testUser.emailVerifySuccess();
			
			UserEntity testUserEntity = UserEntity.from(testUser);
			UserProfileEntity userProfileEntity = UserProfileEntity.from(testUser.getUserProfile());
			UserSettingEntity userSettingEntity = UserSettingEntity.from(testUser.getUserSetting());
			
			testUserEntity.setUserProfile(userProfileEntity);
			testUserEntity.setUserSetting(userSettingEntity);
			
			userRepo.save(testUserEntity);
		}
	}
}
