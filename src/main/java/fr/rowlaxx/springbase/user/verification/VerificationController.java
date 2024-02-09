package fr.rowlaxx.springbase.user.verification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.rowlaxx.springbase.security.scopes.Authenticated;
import fr.rowlaxx.springbase.user.BaseUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/account/verify")
@AllArgsConstructor
@Authenticated(allowVerified = false, allowUnverified = true)
public class VerificationController {
	private VerificationService service;
	
 	@PostMapping("/email/{code}")
	public void verify(BaseUser user, @Min(0) @Max(999_999) @PathVariable int code) {
		service.verifyEmail(user, code);
	}
	
	@GetMapping("/email/sendcode")
	public void getVerificationCode(BaseUser user) {
		service.sendNewEmailVerificationCode(user);
	}
	
	@GetMapping("/email/verifiable")
	public boolean isVerifiable(BaseUser user) {
		return service.isVerifiable(user.getEmailVerificationCode());
	}
}
