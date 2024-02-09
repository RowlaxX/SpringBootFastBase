package fr.rowlaxx.springbase.security.auth;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.security.auth.rememberme.MyRememberMeService;
import fr.rowlaxx.springbase.security.auth.rememberme.PersistentLoginService;
import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import fr.rowlaxx.springbase.user.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {
	private MyRememberMeService rememberMeService;
	private PersistentLoginService persistentLoginService;
	private AuthenticationManager authenticationManager;
	
	public void forceLogin(HttpServletRequest req, HttpServletResponse resp, Authentication auth, boolean remember) {
		var sc = SecurityContextHolder.getContext();
		
		sc.setAuthentication(auth);

		var session = req.getSession(true);
		session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

		rememberMeService.enableRememberMe(req, remember);
		rememberMeService.loginSuccess(req, resp, auth);
	}
	
	public BaseUser login(HttpServletRequest req, HttpServletResponse resp, BaseUser user, boolean remember) {
		forceLogin(req, resp, new UserAuthenticationToken(user), remember);
		return user;
	}
	
	public BaseUser login(HttpServletRequest req, HttpServletResponse resp, String email, String password, boolean remember) {
		var token = new UsernamePasswordAuthenticationToken(email, password);

		try {
			var auth = (UserAuthenticationToken) authenticationManager.authenticate(token);
			forceLogin(req, resp, auth, remember);
			return auth.getPrincipal();
		} catch (AuthenticationException e) {
			rememberMeService.loginFail(req, resp);
			throw e;
		}
	}
	
	public void logout(HttpServletRequest req, HttpServletResponse resp) {
		persistentLoginService.deleteCookie(req, resp);
			
		var session = req.getSession(false);
		if (session != null)
			session.invalidate();
			
		var sc = SecurityContextHolder.getContext();
		sc.setAuthentication(null);
		SecurityContextHolder.clearContext();
	}
}
