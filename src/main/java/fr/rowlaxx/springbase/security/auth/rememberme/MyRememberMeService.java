package fr.rowlaxx.springbase.security.auth.rememberme;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.security.auth.token.RememberMeAuthenticationToken;
import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Service
@AllArgsConstructor
@CommonsLog
public class MyRememberMeService implements RememberMeServices, LogoutHandler {
	private static final String ENABLED = "REMEMBERME_ENABLED";
	
	private PersistentLoginService service;
	
	@Override
	public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
		var rawCookie = service.extractRawCookie(request);
		
		if (rawCookie != null)
			return new RememberMeAuthenticationToken(rawCookie);
		
		service.clearCookie(response);
		return null;
	}
	
	public void enableRememberMe(HttpServletRequest request, boolean enabled) {
		request.setAttribute(ENABLED, enabled);
	}
	
	@Override
	public void loginFail(HttpServletRequest request, HttpServletResponse response) { 
		log.debug("Deleting cookie");
		service.deleteCookie(request, response);
	}

	@Override
	public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		if (!(successfulAuthentication instanceof UserAuthenticationToken userToken))
			return;
		
		var attr = request.getAttribute(ENABLED);
		if (attr != null && !(boolean) attr)
			return;
		
		var user = userToken.getPrincipal();
		var plc = service.createCookieForUser(user);
		response.addCookie(plc.cookie());
	}
	
	public void updateIfNeeded(HttpServletRequest request, HttpServletResponse response) {
		if (service.willRemember(request)) {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			logout(request, response, auth);
			enableRememberMe(request, true);
			loginSuccess(request, response, auth);
		}
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		service.deleteCookie(request, response);
	}
}
