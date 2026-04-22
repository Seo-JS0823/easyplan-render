package com.easyplan._04_infra.mail;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerifiedService {
	@Value("${spring.mail.username}")
	private String from;
	
	private final JavaMailSender mailSender;
	
	private final StringRedisTemplate redisTemplate;
	
	private static final String PREFIX = "verify:";
	
  private static final long LIMIT_HOUR = 24;
	
	public void sendJoinVerification(String toEmail) {
		String emailToken = UUID.randomUUID().toString();
		saveTokneToCache(emailToken, toEmail);
		
		String link = "https://localhost:8085/user/verify?token=" + emailToken;
		sendMail(toEmail, link);
	}
	
	private void saveTokneToCache(String token, String email) {
		redisTemplate.opsForValue().set(PREFIX + token, email, Duration.ofHours(LIMIT_HOUR));
	}
	
	private void sendMail(String toEmail, String link) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setFrom(from, "Easyplan"); 
			helper.setTo(toEmail);
			helper.setSubject("[Easyplan] 회원가입을 위해 이메일 인증을 완료해주세요.");
			
			String htmlContent = String.format(
			    "<div style='margin:20px; padding:20px; border:1px solid #ddd; font-family: sans-serif;'>" +
			    "   <h2 style='color: #03C75A;'>이메일 인증 안내</h2>" +
			    "   <p>안녕하세요! 서비스 가입을 환영합니다.</p>" +
			    "   <p>아래 <b>[인증하기]</b> 버튼을 클릭하시면 가입 절차가 완료됩니다.</p>" +
			    "   <div style='margin-top: 30px;'>" +
			    "       <a href='%s' style='display:inline-block; background:#03C75A; color:#fff; padding:12px 25px; text-decoration:none; border-radius:5px; font-weight:bold;'>인증하기</a>" +
			    "   </div>" +
			    "   <p style='margin-top: 20px; font-size: 0.8em; color: #888;'>본 메일은 발신 전용입니다. 인증 링크는 24시간 동안만 유효합니다.</p>" +
			    "</div>", link);
			
			helper.setText(htmlContent, true);
			mailSender.send(message);
    } catch (Exception e) {
    	throw new RuntimeException("메일 발송 중 오류가 발생했습니다: " + e.getMessage());
    }
	}
	
	public String verifyToken(String token) {
    String email = redisTemplate.opsForValue().get(PREFIX + token);
    if (email == null) {
    	throw new IllegalArgumentException("만료되었거나 유효하지 않은 인증 토큰입니다.");
    }
    
    // 인증 성공 시 토큰 즉시 삭제 (1회용)
    redisTemplate.delete(PREFIX + token);
    return email;
	}
}
