package fr.rowlaxx.springbase.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import fr.rowlaxx.springbase.security.auth.provider.RememberMeAuthenticationProvider;
import fr.rowlaxx.springbase.security.auth.provider.UsernamePasswordAuthenticationProvider;
import fr.rowlaxx.springbase.security.auth.rememberme.MyRememberMeService;
import lombok.AllArgsConstructor;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	private BerrizAccessDeniedHandler accessDeniedHandler;
	private MyRememberMeService rememberMeServices;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
			throws Exception {

		return http.exceptionHandling(c -> c.accessDeniedHandler(accessDeniedHandler))
				.authenticationManager(authenticationManager)
				.rememberMe(c -> c.rememberMeServices(rememberMeServices))
				.csrf(c -> c.disable())// TODO Configure
				.build();

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	ProviderManager providerManager(
			UsernamePasswordAuthenticationProvider berrizUsernamePasswordAuthenticationProvider,
			RememberMeAuthenticationProvider berrizRememberMeAuthenticationProvider) {

		var p = new ProviderManager(berrizRememberMeAuthenticationProvider,
				berrizUsernamePasswordAuthenticationProvider);
		p.setEraseCredentialsAfterAuthentication(false);
		return p;
	}
}
