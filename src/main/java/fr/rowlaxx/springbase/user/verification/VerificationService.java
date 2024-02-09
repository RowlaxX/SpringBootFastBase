package fr.rowlaxx.springbase.user.verification;

import java.time.Duration;

import org.springframework.stereotype.Service;

import fr.rowlaxx.springbase.exception.ApiException;
import fr.rowlaxx.springbase.user.BaseUser;
import fr.rowlaxx.springbase.user.UserJpaRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class VerificationService {
	public static final Duration EXPIRATION_PERIOD = Duration.ofHours(1);
	
	private UserJpaRepository userRepo;
	private VerificationCodeSender sender;
	
	public boolean isVerifiable(VerificationCode code) {
		return code != null && !code.isLocked() && !code.isExpired();
	}
	
	public void checkVerifiable(VerificationCode code) {
		if (code == null)
			throw new ApiException("No verification code has been sent for this user");
		if (code.isLocked())
			throw new ApiException("Maximum attempt reached for this verification code, please generate another one");
		if (code.isExpired())
			throw new ApiException("This verification code has expired, please generate another one");
	}
	
	public void verifyEmail(BaseUser user, int codeValue) {
		var code = user.getEmailVerificationCode();
		checkVerifiable(code);
		code.addOneAttempt();
		
		if (codeValue != code.getValue())
			throw new ApiException("Wrong verification code");
		
		user.setEmailVerified(true);
		user.setEmailVerificationCode(null);
		userRepo.save(user);
	}
	
	private void tryGenerateNewEmailVerificationCode(BaseUser user) {
		var oldCode = user.getEmailVerificationCode();
		
		if (oldCode != null) {
			var remaining = -oldCode.getElapsedDuration().minusSeconds(60).toSeconds();
			
			if (remaining > 0)
				throw new ApiException(
						"Please wait " + remaining + " seconds before generating another code");
		}
		
		var code = new VerificationCode();
		user.setEmailVerificationCode(code);
		code.setExpirationPeriod(EXPIRATION_PERIOD);
		userRepo.save(user);
	}
	
	public void sendNewEmailVerificationCode(BaseUser user) {
		tryGenerateNewEmailVerificationCode(user);
		sender.sendEmailVerificationCodeTo(user);
	}
}
