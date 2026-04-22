package com.easyplan._01_web.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.easyplan._01_web.GlobalResponse;
import com.easyplan.shared.exception.GlobalError;
import com.easyplan.shared.exception.GlobalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(GlobalException.class)
	protected ResponseEntity<?> handleEasyPlanException(GlobalException e, HttpServletRequest request, HttpServletResponse response) {
		GlobalError error = e.getError();
		
		String message = error.getMessage();
		
		log.error("ExceptionHandler Code: [{}]: {}", error.getClass().getSimpleName(), message);
		
		return ResponseEntity
				.status(error.getStatus())
				.body(GlobalResponse.fail(error.getStatus(), message));
	}
}
