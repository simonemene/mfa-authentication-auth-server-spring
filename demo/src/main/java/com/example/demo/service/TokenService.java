package com.example.demo.service;

import com.example.demo.dto.TokenResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TokenService {

	private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(30);
	private static final Duration REFRESH_TOKEN_TTL = Duration.ofHours(4);

	private final JwtEncoder jwtEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final SecureRandom secureRandom = new SecureRandom();

	@Transactional
	public TokenResponse generateTokens(String username) {
		Instant now = Instant.now();

		String accessToken = generateAccessToken(username, now);
		String refreshToken = generateOpaqueRefreshToken();
		String refreshTokenHash = sha256(refreshToken);

		RefreshToken entity = new RefreshToken(
				username,
				refreshTokenHash,
				now,
				now.plus(REFRESH_TOKEN_TTL)
		);

		refreshTokenRepository.save(entity);

		return new TokenResponse(
				accessToken,
				refreshToken,
				"Bearer",
				ACCESS_TOKEN_TTL.toSeconds(),
				REFRESH_TOKEN_TTL.toSeconds()
		);
	}

	@Transactional
	public TokenResponse refresh(String refreshToken) {
		String refreshTokenHash = sha256(refreshToken);

		RefreshToken storedToken = refreshTokenRepository
				.findByTokenHashAndRevokedFalse(refreshTokenHash)
				.orElseThrow(() -> new IllegalArgumentException("Refresh token non valido"));

		Instant now = Instant.now();

		if (storedToken.getExpiresAt().isBefore(now)) {
			storedToken.setRevoked(true);
			refreshTokenRepository.save(storedToken);
			throw new IllegalArgumentException("Refresh token scaduto");
		}
		storedToken.setRevoked(true);
		refreshTokenRepository.save(storedToken);

		return generateTokens(storedToken.getUsername());
	}

	private String generateAccessToken(String username, Instant now) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("http://localhost:8080")
				.issuedAt(now)
				.expiresAt(now.plus(ACCESS_TOKEN_TTL))
				.subject(username)
				.claim("mfa", true)
				.claim("amr", List.of("otp"))
				.claim("scope", "api.read")
				.build();

		return jwtEncoder
				.encode(JwtEncoderParameters.from(claims))
				.getTokenValue();
	}

	private String generateOpaqueRefreshToken() {
		byte[] bytes = new byte[64];
		secureRandom.nextBytes(bytes);

		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(bytes);
	}

	private String sha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (Exception ex) {
			throw new IllegalStateException("Non posso fare l'hash del refresh token", ex);
		}
	}
}