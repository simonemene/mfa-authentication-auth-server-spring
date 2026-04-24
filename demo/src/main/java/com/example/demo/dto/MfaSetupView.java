package com.example.demo.dto;

public record MfaSetupView(
		String qrCodeBase64,
		String secret
) {
}