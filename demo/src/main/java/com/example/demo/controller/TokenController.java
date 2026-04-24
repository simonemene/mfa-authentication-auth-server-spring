package com.example.demo.controller;

import com.example.demo.dto.TokenResponse;
import com.example.demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class TokenController {

	private final TokenService tokenService;

	@PostMapping("/refresh")
	public TokenResponse refresh(@RequestParam String refreshToken) {
		return tokenService.refresh(refreshToken);
	}
}
