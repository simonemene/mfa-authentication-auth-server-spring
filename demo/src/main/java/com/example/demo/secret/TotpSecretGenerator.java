package com.example.demo.secret;

import com.example.demo.properties.SecretProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class TotpSecretGenerator {

	private final SecretProperties secretProperties;
	private final SecureRandom secureRandom = new SecureRandom();

	public String generateSecret() {

		int lunghezzaSegreto = secretProperties.lunghezzaSecret();

		if (secretProperties.caratteri() == null || secretProperties.caratteri().isBlank()) {
			throw new IllegalArgumentException("L'alfabeto non può essere nullo o vuoto");
		}

		if (lunghezzaSegreto <= 0) {
			throw new IllegalArgumentException("La lunghezza del segreto deve essere maggiore di 0");
		}

		StringBuilder sb = new StringBuilder(lunghezzaSegreto);

		for (int i = 0; i < lunghezzaSegreto; i++) {
			int index = secureRandom.nextInt(lunghezzaSegreto);
			sb.append(secretProperties.caratteri().charAt(index));
		}

		return sb.toString();
	}
}