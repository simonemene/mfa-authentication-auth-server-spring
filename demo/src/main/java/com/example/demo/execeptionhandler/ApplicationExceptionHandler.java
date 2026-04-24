package com.example.demo.execeptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler(UsernameNotFoundException.class)
	public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

		problemDetail.setTitle("Authentication failed");
		problemDetail.setDetail("Invalid username or password");
		problemDetail.setProperty("timestamp", Instant.now());

		return problemDetail;
	}
}
