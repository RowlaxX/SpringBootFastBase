package fr.rowlaxx.springbase.mvc;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.rowlaxx.springbase.mvc.interceptor.LoggingInterceptor;
import fr.rowlaxx.springbase.mvc.resolver.UserArgumentResolver;
import fr.rowlaxx.springbase.security.SecurityScopeInterceptor;
import fr.rowlaxx.springbase.security.UserSynchronizerInterceptor;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebMvc
@AllArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
	private LoggingInterceptor loggingInterceptor;
	private SecurityScopeInterceptor securityInterceptor;
	private UserSynchronizerInterceptor userSynchronizerInterceptor;
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new UserArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userSynchronizerInterceptor).order(1000);
		registry.addInterceptor(loggingInterceptor).order(1001);
		registry.addInterceptor(securityInterceptor).order(1002);
	}
}
