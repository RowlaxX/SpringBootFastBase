package fr.rowlaxx.springbase.security.auth.token;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import fr.rowlaxx.springbase.security.auth.rememberme.RawPersistentLoginCookie;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RememberMeAuthenticationToken implements Authentication {
	private static final long serialVersionUID = -2164194221148405395L;
	
	private final RawPersistentLoginCookie rawCookie;
	
	@Override
	public List<GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getCredentials() {
		return rawCookie.data();
	}

	@Override
	public String getDetails() {
		return rawCookie.data();
	}

	@Override
	public RawPersistentLoginCookie getPrincipal() {
		return rawCookie;
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		//Always false
	}

	@Override
	public String getName() {
		return rawCookie.loginUuid().toString();
	}
}
