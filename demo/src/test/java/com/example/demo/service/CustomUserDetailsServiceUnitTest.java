package com.example.demo.service;

import com.example.demo.repository.AppUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsServiceUnitTest {

	@InjectMocks
	private CustomUserDetailsService service;

	@Mock
	private AppUserRepository repository;

	@BeforeEach
	public void init()
	{
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void utenteNonTrovato()
	{
		//given
		Mockito.when(repository.findByUsername("simone")).thenReturn(Optional.empty());
		//when
		//then
		Assertions.assertThatThrownBy(
				()->service.loadUserByUsername("simone")
		).isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("simone");
	}
}
