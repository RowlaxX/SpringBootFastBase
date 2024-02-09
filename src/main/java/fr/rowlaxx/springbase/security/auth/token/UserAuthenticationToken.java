package fr.rowlaxx.springbase.security.auth.token;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import fr.rowlaxx.springbase.user.BaseUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class UserAuthenticationToken implements Authentication {
	private static final long serialVersionUID = -7530853645663422373L;
	
	@Getter @NonNull
	private final UUID userUUID;
	
	@Setter
	private BaseUser user;
	
	@Setter
	private String password;
	
	public UserAuthenticationToken(BaseUser user) {
		this.userUUID = user.getUuid();
		this.password = user.getPassword();
		this.user = user;
	}
	
	public UserAuthenticationToken(UUID userUuid, String password) {
		this.userUUID = userUuid;
		this.password = password;
	}
	
	
	@Override
	public String getName() {
		var u = getPrincipal();
		if (u == null)
			return null;
		return u.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		var u = getPrincipal();
		return u == null ? Collections.emptyList() : u.getAuthorities();
	}

	@Override
	public String getCredentials() {
		return password;
	}

	@Override
	public String getDetails() {
		return "";
	}

	@Override
	public BaseUser getPrincipal() {
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		var u = getPrincipal();
		
		if (u == null || !u.isAccountNonLocked() || !u.isEnabled() || !u.isAccountNonExpired() || !u.isCredentialsNonExpired())
			return false;
		
		return Objects.equals(u.getPassword(), password);
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		var u = getPrincipal();
		if (u == null)
			throw new IllegalArgumentException();
		password = isAuthenticated ? u.getPassword() : null;
	}
}
