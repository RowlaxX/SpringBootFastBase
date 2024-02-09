package fr.rowlaxx.springbase.user.resetpassword;

import fr.rowlaxx.springbase.user.BaseUser;

public interface ResetPasswordCodeSender {

	public void sendResetPasswordCodeTo(BaseUser user);
	
}
