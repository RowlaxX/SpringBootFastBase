package fr.rowlaxx.springbase.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import fr.rowlaxx.springbase.exception.ApiException;
import fr.rowlaxx.springbase.security.auth.AuthenticationService;
import fr.rowlaxx.springbase.security.auth.rememberme.MyRememberMeService;
import fr.rowlaxx.springbase.security.scopes.Authenticated;
import fr.rowlaxx.springbase.user.request.ChangePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Authenticated
public class BaseUserController {
	private BaseUserService userService;
	private MyRememberMeService rememberMeService;
	private AuthenticationService authenticationService;
	
	@PostMapping("/change-password")
	@Authenticated(allowUnverified = true)
	public void changePassword(HttpServletRequest req, HttpServletResponse resp,
			BaseUser user, @Valid @RequestBody ChangePasswordRequest request) {
		if (!userService.isPasswordValid(user, request.currentPassword()))
			throw new ApiException("Wrong current password");

		userService.setPassword(user, request.newPassword());
		rememberMeService.updateIfNeeded(req, resp);
	}
	
	@GetMapping
	@Authenticated(allowUnverified = true)
	public BaseUser get(BaseUser user) {
		return user;
	}
	
	@GetMapping("/home")
	public RedirectView home(BaseUser user) {
		return new RedirectView(user.getHomePageUrl());
	}
	
	@Authenticated(allowUnverified = true, allowVerified = true)
	@PostMapping("/logout")
	public RedirectView logout(HttpServletRequest req, HttpServletResponse resp) {
		authenticationService.logout(req, resp);
		return new RedirectView("https://www.berriz.co/");
	}
}
