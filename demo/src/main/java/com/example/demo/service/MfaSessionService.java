package com.example.demo.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class MfaSessionService {

	public static final String MFA_VERIFIED = "MFA_VERIFIED";

	public void markMfaVerified(HttpSession session) {
		session.setAttribute(MFA_VERIFIED, true);
	}
}