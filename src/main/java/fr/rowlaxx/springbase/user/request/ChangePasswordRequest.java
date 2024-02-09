package fr.rowlaxx.springbase.user.request;

import fr.rowlaxx.springbase.validation.constraints.RawPassword;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(
		@NotNull String currentPassword,
		@NotNull @RawPassword String newPassword) {}
