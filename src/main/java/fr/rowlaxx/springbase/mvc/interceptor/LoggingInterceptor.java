package fr.rowlaxx.springbase.mvc.interceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import fr.rowlaxx.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@Component
@AllArgsConstructor
@CommonsLog
public class LoggingInterceptor implements HandlerInterceptor {
	private final Map<String, Instant> cache = new ConcurrentHashMap<>();
	
	@Override
	public boolean preHandle(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
			Object handler) throws Exception {
		
		var id = request.getRequestId();
		cache.put(id, Instant.now());
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		var start = cache.remove(request.getRequestId());
		var uri = request.getRequestURI();
		var address = request.getRemoteAddr();
		var user = Utils.getCurrentUser();
		var name = user.map(u -> ("[" + u.getEmail() + "] ")).orElse("");
		var duration = Utils.substract(Instant.now(), start);
		var durationStr = duration.toMillis() + "." + ((duration.toNanosPart() % 1_000_000) / 100_000) + "ms";	
		var str = "[" + durationStr + "] [" + address + "] " + name + uri;
		
		if (ex == null)
			log.info(str);
		else
			log.warn(str + " -> " + ex.getLocalizedMessage());
	}
}
