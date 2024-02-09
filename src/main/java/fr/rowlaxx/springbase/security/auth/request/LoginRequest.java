package fr.rowlaxx.springbase.security.auth.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
		@NotNull String email, 
		@NotNull String password,
		boolean rememberme
) {}
