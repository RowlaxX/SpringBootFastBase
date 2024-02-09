package fr.rowlaxx.springbase.user.verification;

import fr.rowlaxx.springbase.user.BaseUser;

public interface VerificationCodeSender {

	public void sendEmailVerificationCodeTo(BaseUser user);
	
}
