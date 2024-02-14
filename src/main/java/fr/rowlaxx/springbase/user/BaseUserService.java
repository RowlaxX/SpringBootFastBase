package fr.rowlaxx.springbase.user;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaseUserService {
	static boolean VERIFY_EMAIL;
	static boolean VERIFY_PHONE;
	
	@Value("${fr.rowlaxx.springbase.user.verify.email:true}") 
	private boolean verifyEmail;
	
	@Value("${fr.rowlaxx.springbase.user.verify.phone:true}") 
	private boolean verifyPhone;
	
	private final PasswordEncoder passwordEncoder;
	
	public void setPassword(BaseUser user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof UserAuthenticationToken userAuth && Objects.equals(userAuth.getUserUUID(), user.getUuid()))
			userAuth.setPassword(user.getPassword());
	}
	
	public boolean isPasswordValid(BaseUser user, String password) {
		return passwordEncoder.matches(password, user.getPassword());
	}
	
	@PostConstruct
	private void init() {
		VERIFY_EMAIL = verifyEmail;
		VERIFY_PHONE = verifyPhone;
	}
}
