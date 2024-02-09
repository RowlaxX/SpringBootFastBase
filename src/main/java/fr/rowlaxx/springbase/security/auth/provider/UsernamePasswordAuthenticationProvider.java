package fr.rowlaxx.springbase.security.auth.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import fr.rowlaxx.springbase.user.BaseUserService;
import fr.rowlaxx.springbase.user.UserJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Component
@AllArgsConstructor
@CommonsLog
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
	private UserJpaRepository<?> userRepository;
	private BaseUserService userService;
	
	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		var email = authentication.getPrincipal().toString();
		var password = authentication.getCredentials().toString();
		
		var user = userRepository.findByEmail(email);
		if (user == null) {
			log.debug("Wrong email");
			throw new BadCredentialsException("Wrong email");
		}
		
		if (!userService.isPasswordValid(user, password)) {
			log.debug("Wrong password");
			throw new BadCredentialsException("Wrong password");
		}
		
		if (!user.isAccountNonLocked())
			throw new BadCredentialsException(user.getBannedReason().getFullDescription());
		
		log.debug("Login successfull for " + user.getEmail());
		return new UserAuthenticationToken(user);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}
}
