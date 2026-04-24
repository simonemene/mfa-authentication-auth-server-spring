package com.example.demo.secret;

import com.example.demo.baseconfig.MockContextConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TotpSecretGeneratorIntegrationTest extends MockContextConfig {

	@Autowired
	private TotpSecretGenerator generator;

	@Test
	public void generator()
	{
		//given
		//when
		String codiceGenerato = generator.generateSecret();
		//then
		Assertions.assertThat(codiceGenerato.length()).isEqualTo(32);
		Assertions.assertThat(codiceGenerato).matches("^[A-Z2-7]+$");
	}


}
