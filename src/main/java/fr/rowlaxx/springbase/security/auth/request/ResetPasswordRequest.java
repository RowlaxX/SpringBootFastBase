package fr.rowlaxx.springbase.security.auth.request;

import fr.rowlaxx.springbase.validation.constraints.RawPassword;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
		@NotNull @RawPassword String newPassword, 
		boolean rememberme
) {}
