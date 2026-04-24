package com.example.demo.controller;

import com.example.demo.entity.AppUser;
import com.example.demo.repository.AppUserRepository;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final AppUserRepository userRepository;

	public HomeController(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/")
	public String home(Principal principal) {
		if (principal == null) {
			return "error";
		}

		AppUser user = userRepository.findByUsername(principal.getName())
				.orElseThrow();

		if (!user.isMfaEnabled()) {
			return "redirect:/mfa/setup";
		}

		return "redirect:/mfa";
	}
}