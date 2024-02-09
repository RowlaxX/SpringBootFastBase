package fr.rowlaxx.springbase.security.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import fr.rowlaxx.springbase.security.auth.request.LoginRequest;
import fr.rowlaxx.springbase.security.scopes.Anonymous;
import fr.rowlaxx.springbase.security.scopes.Public;
import fr.rowlaxx.springbase.user.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
	private AuthenticationService authenticationService;

	@Anonymous
	@PostMapping("/login")
	public RedirectView login(HttpServletRequest req, HttpServletResponse resp,
			@Valid @RequestBody LoginRequest loginRequest) {
		var user = authenticationService.login(req, resp, 
				loginRequest.email(), 
				loginRequest.password(), 
				loginRequest.rememberme());
		return new RedirectView(user.getHomePageUrl());
	}
	
	@Public
	@GetMapping("/logged")
	public boolean logged(BaseUser user) {
		return user != null;
	}
}
