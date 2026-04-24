package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

	protected RefreshToken() {
	}

	public RefreshToken(
			String username,
			String tokenHash,
			Instant issuedAt,
			Instant expiresAt
	) {
		this.username = username;
		this.tokenHash = tokenHash;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
		this.revoked = false;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, unique = true, length = 128)
	private String tokenHash;

	@Column(nullable = false)
	private Instant issuedAt;

	@Column(nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean revoked;
}