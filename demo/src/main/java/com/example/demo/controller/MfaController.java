package com.example.demo.controller;

import com.example.demo.dto.MfaSetupView;
import com.example.demo.dto.TokenResponse;
import com.example.demo.service.MfaService;
import com.example.demo.service.MfaSessionService;
import com.example.demo.service.TokenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mfa")
public class MfaController {

	private final MfaService mfaService;
	private final MfaSessionService mfaSessionService;
	private final TokenService tokenService;



	@GetMapping("/setup")
	public String setup(Authentication authentication, Model model) {
		MfaSetupView setupView = mfaService.prepareSetup(authentication.getName());

		model.addAttribute("qrCode", setupView.qrCodeBase64());

		return "mfa-setup";
	}


	@PostMapping("/setup/verify")
	public String verifyToken(
			@RequestParam String code,
			HttpSession session,
			Model model,
			Authentication authentication
	) {
		String username = authentication.getName();

		boolean valid = mfaService.verifySetup(username, code);

		if (!valid) {
			model.addAttribute("error", "Codice non valido. Riprova.");

			MfaSetupView setupView = mfaService.prepareSetup(username);
			model.addAttribute("qrCode", setupView.qrCodeBase64());

			return "mfa-setup-verify";
		}

		session.setAttribute("MFA_VERIFIED", true);

		TokenResponse tokenResponse = tokenService.generateTokens(username);

		model.addAttribute("accessToken", tokenResponse.accessToken());
		model.addAttribute("refreshToken", tokenResponse.refreshToken());
		model.addAttribute("tokenType", tokenResponse.tokenType());
		model.addAttribute("expiresInSeconds", tokenResponse.expiresInSeconds());
		model.addAttribute("refreshExpiresInSeconds", tokenResponse.refreshExpiresInSeconds());

		return "mfa-ok";
	}

	@GetMapping
	public String mfaPage() {
		return "mfa";
	}

	@PostMapping("/verify")
	public String verify(
			@RequestParam String code,
			HttpSession session,
			Model model,
			Authentication authentication
	) {
		String username = authentication.getName();

		boolean valid = mfaService.verifyLogin(username, code);

		if (!valid) {
			model.addAttribute("error", "Codice MFA non valido");
			return "mfa";
		}

		session.setAttribute("MFA_VERIFIED", true);

		TokenResponse tokenResponse = tokenService.generateTokens(username);

		model.addAttribute("accessToken", tokenResponse.accessToken());
		model.addAttribute("refreshToken", tokenResponse.refreshToken());
		model.addAttribute("tokenType", tokenResponse.tokenType());
		model.addAttribute("expiresInSeconds", tokenResponse.expiresInSeconds());
		model.addAttribute("refreshExpiresInSeconds", tokenResponse.refreshExpiresInSeconds());

		return "mfa-ok";
	}

	@PostMapping("/setup/confirm")
	public String confirmSetup() {
		return "mfa-setup-verify";
	}

}