package fr.rowlaxx.springbase.security.scopes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.rowlaxx.springbase.user.BaseUser;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {

	public Class<? extends BaseUser>[] target() default {BaseUser.class};
	
	public boolean allowVerified() default true;
	public boolean allowUnverified() default false;
	
}
