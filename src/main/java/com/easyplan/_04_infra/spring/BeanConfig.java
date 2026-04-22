package com.easyplan._04_infra.spring;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.easyplan._03_domain.auth.repository.AuthRepository;
import com.easyplan._03_domain.auth.service.AuthService;
import com.easyplan._03_domain.auth.service.TokenService;
import com.easyplan._03_domain.ledger.repository.JournalQueryRepository;
import com.easyplan._03_domain.ledger.repository.JournalRepository;
import com.easyplan._03_domain.ledger.repository.LedgerQueryRepository;
import com.easyplan._03_domain.ledger.repository.LedgerRepository;
import com.easyplan._03_domain.ledger.service.JournalQueryService;
import com.easyplan._03_domain.ledger.service.JournalService;
import com.easyplan._03_domain.ledger.service.LedgerQueryService;
import com.easyplan._03_domain.ledger.service.LedgerService;
import com.easyplan._03_domain.user.repository.UserRepository;
import com.easyplan._03_domain.user.service.PasswordService;
import com.easyplan._03_domain.user.service.UserService;

@Configuration
public class BeanConfig {

	@Bean
	Executor dashboardExecutor() {
		ThreadFactory factory = r -> {
			Thread t = new Thread(r);
			t.setName("ledger-thread-");
			return t;
		};
		return Executors.newFixedThreadPool(40, factory);
	}
	
	@Bean
	UserService userService(UserRepository repo, PasswordService passwordService) {
		return new UserService(repo, passwordService);
	}
	
	@Bean
	AuthService authService(AuthRepository repo, TokenService tokenService) {
		return new AuthService(repo, tokenService);
	}
	
	@Bean
	LedgerService ledgerService(LedgerRepository repo) {
		return new LedgerService(repo);
	}
	
	@Bean
	JournalService journalService(JournalRepository repo) {
		return new JournalService(repo);
	}
	
	@Bean
	LedgerQueryService ledgerQueryService(LedgerQueryRepository repo, LedgerRepository ledgerRepo, Executor dashboardExecutor) {
		return new LedgerQueryService(ledgerRepo, repo, dashboardExecutor);
	}
	
	@Bean
	JournalQueryService journalQueryService(JournalQueryRepository journalQR) {
		return new JournalQueryService(journalQR);
	}
}
