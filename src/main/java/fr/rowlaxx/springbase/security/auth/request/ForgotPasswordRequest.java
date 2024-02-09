package fr.rowlaxx.springbase.security.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ForgotPasswordRequest(
		@NotNull @Email String email
) {}
