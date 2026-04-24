package com.example.demo.service;

import com.example.demo.baseconfig.MockContextConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {"classpath:delete.sql","classpath:sql/service/user-service.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class CustomUserDetailsServiceIntegrationTest extends MockContextConfig {

	@Autowired
	private CustomUserDetailsService service;

	private final static String nome = "simone";

	@Test
	public void searchUser()
	{
		//given
		//when
		UserDetails user = service.loadUserByUsername(nome);
		//then
		Assertions.assertThat(user.getPassword()).isEqualTo("$2a$10$7EqJtq98hPqEX7fNZaFWoOHiilr0JCdEJ7oU5GxYaYkRkYg0VdG5e");
		Assertions.assertThat(user.getUsername()).isEqualTo("simone");
		Assertions.assertThat(user.getAuthorities().size()).isEqualTo(1);
		Assertions.assertThat(user.getAuthorities().stream().findFirst().get()).isEqualTo(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Test
	public void utenteVuoto()
	{
		//given
		//when
		//then
		Assertions.assertThatThrownBy(
				()->service.loadUserByUsername("")
		).isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("");

	}
}
