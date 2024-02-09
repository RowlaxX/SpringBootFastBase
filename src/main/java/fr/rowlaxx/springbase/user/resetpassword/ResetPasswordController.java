package fr.rowlaxx.springbase.user.resetpassword;

import static fr.rowlaxx.springbase.user.resetpassword.ResetPasswordCodeService.COOKIE_NAME;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import fr.rowlaxx.springbase.security.auth.AuthenticationService;
import fr.rowlaxx.springbase.security.auth.request.ForgotPasswordRequest;
import fr.rowlaxx.springbase.security.auth.request.ResetPasswordRequest;
import fr.rowlaxx.springbase.security.scopes.Anonymous;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class ResetPasswordController {
	private ResetPasswordCodeService resetPasswordCodeService;
	private AuthenticationService authenticationService;

	@Anonymous
	@PostMapping("/forgot-password")
	public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		resetPasswordCodeService.forgotPassword(request.email());
	}

	@Anonymous
	@GetMapping("/reset-password/{value}")
	public RedirectView resetPasswordRedirect(HttpServletResponse response, @PathVariable String value) {
		var cookie = resetPasswordCodeService.genResetPasswordCookie("/reset-password", value);
		response.addCookie(cookie);
		return new RedirectView("https://www.berriz.co/reset-password");
	}

	@Anonymous
	@PostMapping("/reset-password")
	public RedirectView resetPassword(HttpServletRequest req, HttpServletResponse resp,
			@CookieValue(name = COOKIE_NAME, required = false) String cookie,
			@Valid @RequestBody ResetPasswordRequest request) {
		
		var user = resetPasswordCodeService.resetPassword(cookie, request.newPassword());
		var newCookie = resetPasswordCodeService.genDummyResetPasswordCookie("/reset-password");
		resp.addCookie(newCookie);
		
		authenticationService.login(req, resp, user, request.rememberme());
		return new RedirectView(user.getHomePageUrl());
	}
	
}
