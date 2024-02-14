package fr.rowlaxx.springbase.user.resetpassword;

import java.time.Duration;

import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.exception.ApiException;
import fr.rowlaxx.springbase.exception.InternalException;
import fr.rowlaxx.springbase.user.BaseUser;
import fr.rowlaxx.springbase.user.BaseUserService;
import fr.rowlaxx.springbase.user.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class ResetPasswordCodeService {
	public static final String COOKIE_NAME = "reset-password";
	public static final Duration EXPIRATION_PERIOD = Duration.ofHours(1);
	
	private ResetPasswordCodeRepository repo;
	private UserJpaRepository userRepo;
	private BaseUserService userService;
	private ResetPasswordCodeSender sender;
	
	private void tryGenerateNewCode(BaseUser user) {
		var oldCode = user.getResetPasswordCode();
		
		if (oldCode != null) {
			var remaining = -oldCode.getElapsedDuration().minusSeconds(120).toSeconds();
			
			if (remaining > 0)
				throw new ApiException(
						"Please wait " + remaining + " seconds before requesting another request");
		}
		
		var code = new ResetPasswordCode();
		code.setExpirationPeriod(EXPIRATION_PERIOD);
		user.setResetPasswordCode(code);
		code.setUserUuid(user.getUuid());
		userRepo.save(user);
	}
	
	public void forgotPassword(BaseUser user) {
		tryGenerateNewCode(user);
		sender.sendResetPasswordCodeTo(user);
	}
	
	public void forgotPassword(String email) {
		var user = userRepo.findByEmail(email);
		if (user == null)
			throw new ApiException("Wrong email");
		forgotPassword(user);
	}
	
	public Cookie genResetPasswordCookie(String path, String value) {
		var cookie = new Cookie(COOKIE_NAME, value);
		cookie.setMaxAge((int)EXPIRATION_PERIOD.toSeconds());
		cookie.setHttpOnly(true);
		cookie.setPath(path);
		return cookie;
	}
	
	public Cookie genDummyResetPasswordCookie(String path) {
		var cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath(path);
		return cookie;
	}
	
	public ResetPasswordCode findValidCodeByValue(String value) {
		var reset = repo.findByValue(value);
		if (reset == null)
			throw new ApiException("Illegal reset password cookie");
		if (reset.isExpired())
			throw new ApiException("The reset code has been expired");
		return reset;
	}
	
	public BaseUser resetPassword(String cookie, String password) {
		if (cookie == null)
			throw new ApiException("No reset password cookie");

		var reset = findValidCodeByValue(cookie);
		var userUuid = reset.getUserUuid();
		var user = (BaseUser) userRepo.findByUuid(userUuid);
		if (user == null)
			throw new InternalException("No user found");
		
		user.setResetPasswordCode(null);
		userService.setPassword(user, password);
		userRepo.save(user);
		
		return user;
	}
}
