package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.time.Instant;

@Service
public class TotpService {

	private static final int TIME_STEP_SECONDS = 30;
	private static final int CODE_DIGITS = 6;

	private final AppUserRepository userRepository;
	private final TotpSecretCryptoService cryptoService;

	public TotpService(
			AppUserRepository userRepository,
			TotpSecretCryptoService cryptoService
	) {
		this.userRepository = userRepository;
		this.cryptoService = cryptoService;
	}

	public boolean verifyCode(String username, String submittedCode) {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow();

		String encryptedSecret = user.getTotpSecretEncrypted();

		if (encryptedSecret == null || encryptedSecret.isBlank()) {
			return false;
		}

		String plainSecret = cryptoService.decrypt(encryptedSecret);

		String normalizedCode = submittedCode == null
				? ""
				: submittedCode.trim().replace(" ", "");

		long currentWindow = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;

		for (long window = currentWindow - 1; window <= currentWindow + 1; window++) {
			String expectedCode = generateCode(plainSecret, window);

			System.out.println("DEBUG TOTP expected=" + expectedCode
					+ " submitted=" + normalizedCode
					+ " window=" + window
					+ " secret=" + plainSecret);

			if (constantTimeEquals(expectedCode, normalizedCode)) {
				return true;
			}
		}

		return false;
	}

	private String generateCode(String base32Secret, long counter) {
		try {
			byte[] key = decodeBase32(base32Secret);

			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.putLong(counter);
			byte[] counterBytes = buffer.array();

			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));

			byte[] hash = mac.doFinal(counterBytes);

			int offset = hash[hash.length - 1] & 0x0F;

			int binary =
					((hash[offset] & 0x7F) << 24)
							| ((hash[offset + 1] & 0xFF) << 16)
							| ((hash[offset + 2] & 0xFF) << 8)
							| (hash[offset + 3] & 0xFF);

			int otp = binary % (int) Math.pow(10, CODE_DIGITS);

			return String.format("%06d", otp);

		} catch (Exception ex) {
			throw new IllegalStateException("Cannot generate TOTP code", ex);
		}
	}

	private byte[] decodeBase32(String base32) {
		String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

		String normalized = base32
				.replace("=", "")
				.replace(" ", "")
				.toUpperCase();

		int buffer = 0;
		int bitsLeft = 0;

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		for (char c : normalized.toCharArray()) {
			int val = base32Chars.indexOf(c);

			if (val < 0) {
				throw new IllegalArgumentException("Invalid Base32 character: " + c);
			}

			buffer <<= 5;
			buffer |= val & 31;
			bitsLeft += 5;

			if (bitsLeft >= 8) {
				output.write((buffer >> (bitsLeft - 8)) & 0xFF);
				bitsLeft -= 8;
			}
		}

		return output.toByteArray();
	}

	private boolean constantTimeEquals(String a, String b) {
		if (a == null || b == null) {
			return false;
		}

		if (a.length() != b.length()) {
			return false;
		}

		int result = 0;

		for (int i = 0; i < a.length(); i++) {
			result |= a.charAt(i) ^ b.charAt(i);
		}

		return result == 0;
	}
}