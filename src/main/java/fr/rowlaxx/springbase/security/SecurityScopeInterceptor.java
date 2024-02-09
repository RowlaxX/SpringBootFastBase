package fr.rowlaxx.springbase.security;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import fr.rowlaxx.springbase.exception.RedirectException;
import fr.rowlaxx.springbase.security.scopes.Anonymous;
import fr.rowlaxx.springbase.security.scopes.Authenticated;
import fr.rowlaxx.springbase.security.scopes.Private;
import fr.rowlaxx.springbase.security.scopes.Public;
import fr.rowlaxx.springbase.user.BaseUser;
import fr.rowlaxx.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SecurityScopeInterceptor implements HandlerInterceptor {
	private static final Authenticated DEFAULT_SCOPE = Private.class.getAnnotation(Authenticated.class);

	@SuppressWarnings("unchecked")
	private static final Class<? extends Annotation>[] SCOPES = new Class[] { Authenticated.class, Anonymous.class,
			Public.class, Private.class};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (handler instanceof HandlerMethod method) {
			var sc = SecurityContextHolder.getContext();
			var auth = sc.getAuthentication();
			var anno = getAnnotation(method);
			var user = Utils.getCurrentUser().orElse(null);
			
			if (anno instanceof Authenticated a)
				handle(a, user);
			else if (anno instanceof Public)
				handlePublic(auth);
			else if (anno instanceof Anonymous)
				handleAnonymous(user);
		}

		return true;
	}

	private static Annotation getAnnotation(HandlerMethod method) {
		var a = findAnnotation(method, method::getMethodAnnotation);
		if (a == null)
			a = findAnnotation(method, method.getBeanType()::getAnnotation);
		if (a == null || a instanceof Private)
			a = DEFAULT_SCOPE;
		return a;
	}

	private static Annotation findAnnotation(HandlerMethod method,
			Function<Class<? extends Annotation>, Annotation> getAnnotation) {

		List<Annotation> list = new ArrayList<>(1);
		Annotation a = null;

		for (var t : SCOPES)
			if ((a = getAnnotation.apply(t)) != null)
				list.add(a);

		return switch (list.size()) {
		case 0 -> null;
		case 1 -> list.get(0);
		default -> throw new IllegalStateException("Multiple scope detected : " + method);
		};
	}

	public void handle(Authenticated anno, BaseUser user) {
		if (user == null)
			throw new RedirectException("https://www.berriz.co/login");

		if (!anno.allowVerified() && user.isEmailVerified())
			throw new AccessDeniedException("You cannot do that since your account has been verified");
		if (!anno.allowUnverified() && !user.isEmailVerified())
			throw new AccessDeniedException("{/account/verify}You need to verify your account first");

		
		
		for (var clazz : anno.target())
			if (clazz.isInstance(user))
				return;
		
		throw new AccessDeniedException("Access denied");
	}

	public void handleAnonymous(BaseUser user) {
		if (user != null)
			throw new AccessDeniedException("You cannot do that since you are already logged in");
	}

	public void handlePublic(Authentication auth) {
		// Permit all
	}
}
