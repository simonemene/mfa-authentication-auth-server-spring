package com.example.demo.service;

import com.example.demo.dto.MfaSetupView;
import com.example.demo.entity.AppUser;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.secret.TotpSecretGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MfaService {

	private static final String ISSUER = "DemoAuthServer";

	private final AppUserRepository userRepository;
	private final TotpSecretGenerator secretGenerator;
	private final TotpSecretCryptoService cryptoService;
	private final QrCodeService qrCodeService;
	private final TotpService totpService;

	public MfaService(
			AppUserRepository userRepository,
			TotpSecretGenerator secretGenerator,
			TotpSecretCryptoService cryptoService,
			QrCodeService qrCodeService,
			TotpService totpService
	) {
		this.userRepository = userRepository;
		this.secretGenerator = secretGenerator;
		this.cryptoService = cryptoService;
		this.qrCodeService = qrCodeService;
		this.totpService = totpService;
	}

	@Transactional
	public MfaSetupView prepareSetup(String username) {
		AppUser user = findUser(username);

		String plainSecret = getOrCreatePlainSecret(user);

		String otpAuthUri = buildOtpAuthUri(
				ISSUER,
				user.getUsername(),
				plainSecret
		);

		String qrCodeBase64 = qrCodeService.generateQrCodeBase64(otpAuthUri);

		return new MfaSetupView(qrCodeBase64, plainSecret);
	}

	@Transactional
	public boolean verifySetup(String username, String code) {
		boolean valid = totpService.verifyCode(username, code);

		if (!valid) {
			return false;
		}

		AppUser user = findUser(username);
		user.setMfaEnabled(true);
		userRepository.save(user);

		return true;
	}

	public boolean verifyLogin(String username, String code) {
		return totpService.verifyCode(username, code);
	}

	private String getOrCreatePlainSecret(AppUser user) {
		if (user.getTotpSecretEncrypted() != null) {
			return cryptoService.decrypt(user.getTotpSecretEncrypted());
		}

		String plainSecret = secretGenerator.generateSecret();

		user.setTotpSecretEncrypted(cryptoService.encrypt(plainSecret));
		user.setMfaEnabled(false);

		userRepository.save(user);

		return plainSecret;
	}

	private AppUser findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException("User not found: " + username));
	}

	private String buildOtpAuthUri(String issuer, String username, String secret) {
		return "otpauth://totp/"
				+ urlEncode(issuer + ":" + username)
				+ "?secret=" + urlEncode(secret)
				+ "&issuer=" + urlEncode(issuer)
				+ "&algorithm=SHA1"
				+ "&digits=6"
				+ "&period=30";
	}

	private String urlEncode(String value) {
		return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
				.replace("+", "%20");
	}
}