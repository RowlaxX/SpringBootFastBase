package fr.rowlaxx.springbase.security;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import fr.rowlaxx.springbase.user.BaseUser;
import fr.rowlaxx.springbase.user.UserJpaRepository;
import fr.rowlaxx.utils.JobTable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Component
@AllArgsConstructor
@CommonsLog
public class UserSynchronizerInterceptor implements HandlerInterceptor {
	private static final String LOCKED = "USER_LOCKED";
	
	private final JobTable<UUID> jobTable = new JobTable<>();
	private UserJpaRepository<? extends BaseUser> userRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		var auth = SecurityContextHolder.getContext().getAuthentication();
			
		if (auth instanceof UserAuthenticationToken userToken) {
			var uuid = userToken.getUserUUID();
			log.debug("Locking user " + uuid);
			jobTable.use(uuid);
				
			if (userToken.getPrincipal() == null) {
				var user = userRepository.findByUuid(uuid);
				userToken.setUser(user);
			}
			
			request.setAttribute(LOCKED, true);
		}
		
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		var auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth instanceof UserAuthenticationToken userToken) {
			userToken.setUser(null);
			
			if (Objects.equals(request.getAttribute(LOCKED), true)) {
				var uuid = userToken.getUserUUID();
				log.debug("Freeing user " + uuid);
				jobTable.free(uuid);
			}
		}
	}
}
