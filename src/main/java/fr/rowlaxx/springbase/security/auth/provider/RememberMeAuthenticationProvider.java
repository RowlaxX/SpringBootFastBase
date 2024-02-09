package fr.rowlaxx.springbase.security.auth.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import fr.rowlaxx.springbase.security.auth.rememberme.PersistentLoginService;
import fr.rowlaxx.springbase.security.auth.token.RememberMeAuthenticationToken;
import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Component
@AllArgsConstructor
@CommonsLog
public class RememberMeAuthenticationProvider implements AuthenticationProvider {
	private PersistentLoginService service;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		var auth = (RememberMeAuthenticationToken) authentication;
		var rawCookie = auth.getPrincipal();
		var cookie = service.fetch(rawCookie);
		
		if (cookie == null) {
			log.debug("Unknown remember me token");
			throw new BadCredentialsException("This remember me token is unknown");
		}
		
		var login = cookie.login();
		var userUuid = login.getUserUuid();
		var dbPassword = cookie.dbPassword();
		var data = cookie.data();

		log.debug("Trying rememberme login for user " + userUuid);

		
		
		if (login.isExpired()) {
			log.debug("Expired remember me token");
			throw new CredentialsExpiredException("This remember me token has been expired");
		}
		else if (!login.isValid(dbPassword, data)) {
			log.debug("Invalid remember me token");
			throw new BadCredentialsException("This remember me token is invalid");
		}
		
		log.debug("Rememberme login successfull for " + userUuid);
		return new UserAuthenticationToken(userUuid, dbPassword);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == RememberMeAuthenticationToken.class;
	}
}
