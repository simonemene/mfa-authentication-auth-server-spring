package com.example.demo.controller;

import com.example.demo.dto.MfaSetupView;
import com.example.demo.service.MfaService;
import com.example.demo.service.MfaSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mfa")
public class MfaController {

	private final MfaService mfaService;
	private final MfaSessionService mfaSessionService;

	public MfaController(
			MfaService mfaService,
			MfaSessionService mfaSessionService
	) {
		this.mfaService = mfaService;
		this.mfaSessionService = mfaSessionService;
	}

	@GetMapping("/setup")
	public String setup(Authentication authentication, Model model) {
		MfaSetupView setupView = mfaService.prepareSetup(authentication.getName());

		model.addAttribute("qrCode", setupView.qrCodeBase64());

		return "mfa-setup";
	}

	@PostMapping("/setup/verify")
	public String verifySetup(
			@RequestParam String code,
			HttpSession session,
			Model model,
			Authentication authentication
	) {
		boolean valid = mfaService.verifySetup(authentication.getName(), code);

		if (!valid) {
			model.addAttribute("error", "Codice non valido. Riprova.");
			return "mfa-setup-verify";
		}

		session.setAttribute("MFA_VERIFIED", true);

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

		boolean valid = mfaService.verifyLogin(authentication.getName(), code);

		if (!valid) {
			model.addAttribute("error", "Codice MFA non valido");
			return "mfa";
		}

		mfaSessionService.markMfaVerified(session);

		return "redirect:/";
	}

	@PostMapping("/setup/confirm")
	public String confirmSetup() {
		return "mfa-setup-verify";
	}
}