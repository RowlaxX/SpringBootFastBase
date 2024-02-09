package fr.rowlaxx.springbase.user;

import java.util.Objects;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BaseUserService {
	private PasswordEncoder passwordEncoder;
	
	public void setPassword(BaseUser user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof UserAuthenticationToken userAuth && Objects.equals(userAuth.getUserUUID(), user.getUuid()))
			userAuth.setPassword(user.getPassword());
	}
	
	public boolean isPasswordValid(BaseUser user, String password) {
		return passwordEncoder.matches(password, user.getPassword());
	}
}
