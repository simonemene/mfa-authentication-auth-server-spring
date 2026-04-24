package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TotpSecretCryptoService {

	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BIT = 128;

	private final SecretKeySpec keySpec;
	private final SecureRandom secureRandom = new SecureRandom();

	public TotpSecretCryptoService(
			@Value("${security.mfa.encryption-key}") String base64Key
	) {
		byte[] key = Base64.getDecoder().decode(base64Key);

		if (key.length != 32) {
			throw new IllegalArgumentException("MFA encryption key must be 256 bit / 32 bytes");
		}

		this.keySpec = new SecretKeySpec(key, "AES");
	}

	public String encrypt(String plainText) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(
					Cipher.ENCRYPT_MODE,
					keySpec,
					new GCMParameterSpec(TAG_LENGTH_BIT, iv)
			);

			byte[] cipherText = cipher.doFinal(plainText.getBytes());

			byte[] result = new byte[iv.length + cipherText.length];

			System.arraycopy(iv, 0, result, 0, iv.length);
			System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

			return Base64.getEncoder().encodeToString(result);

		} catch (Exception ex) {
			throw new IllegalStateException("Cannot encrypt TOTP secret", ex);
		}
	}

	public String decrypt(String encryptedValue) {
		try {
			byte[] decoded = Base64.getDecoder().decode(encryptedValue);

			byte[] iv = new byte[IV_LENGTH];
			byte[] cipherText = new byte[decoded.length - IV_LENGTH];

			System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
			System.arraycopy(decoded, IV_LENGTH, cipherText, 0, cipherText.length);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(
					Cipher.DECRYPT_MODE,
					keySpec,
					new GCMParameterSpec(TAG_LENGTH_BIT, iv)
			);

			byte[] plainText = cipher.doFinal(cipherText);

			return new String(plainText);

		} catch (Exception ex) {
			throw new IllegalStateException("Cannot decrypt TOTP secret", ex);
		}
	}
}
