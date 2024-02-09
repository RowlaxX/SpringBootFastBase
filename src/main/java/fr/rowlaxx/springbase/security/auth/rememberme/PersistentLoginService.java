package fr.rowlaxx.springbase.security.auth.rememberme;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.rowlaxx.springbase.user.BaseUser;
import fr.rowlaxx.springbase.user.UserJpaRepository;
import fr.rowlaxx.utils.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PersistentLoginService {
	private static final String COOKIE_NAME = "remember-me";

	private UserJpaRepository<? extends BaseUser> userRepo;
	private PersistentLoginRepository loginRepo;
	
	public boolean willRemember(HttpServletRequest request) {
		return Utils.extractCookie(request, COOKIE_NAME) != null;
	}
	
	@Transactional
	public PersistentLoginCookie createCookieForUser(BaseUser user) {
		var pl = new PersistentLogin();
		pl.setUserUuid(user.getUuid());
		pl.setExpirationPeriod(Duration.ofDays(60));
		loginRepo.save(pl);
		
		var data = pl.encryptPassword(user.getPassword());
		var cookie = new Cookie(COOKIE_NAME, pl.getUuid() + "|" + data);
		cookie.setMaxAge((int)Duration.ofDays(60).toSeconds());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		
		return new PersistentLoginCookie(pl, cookie, data, user.getPassword());
	}
	
	public RawPersistentLoginCookie extractRawCookie(HttpServletRequest request) {
		var cookie = Utils.extractCookie(request, COOKIE_NAME);
		if (cookie == null)
			return null;
		
		var value = cookie.getValue();
		
		var splitted = value.split("\\|", 2);
		if (splitted.length != 2)
			return null;
		
		try {
			var uuid = UUID.fromString(splitted[0]);
			var data = splitted[1];
			return new RawPersistentLoginCookie(uuid, cookie, data);
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	public void clearCookie(HttpServletResponse response) {
		Utils.clearCookie(response, COOKIE_NAME, null);
	}
	
	@Transactional
	public void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
		var rawCookie = extractRawCookie(request);
		if (rawCookie != null)
			loginRepo.deleteById(rawCookie.loginUuid());
		clearCookie(response);
	}
	
	@Transactional
	public PersistentLoginCookie fetch(RawPersistentLoginCookie cookie) {
		var login = loginRepo.findByUuid(cookie.loginUuid());
		if (login == null)
			return null;
		
		var password = userRepo.findPasswordByUuid(login.getUserUuid());
		return new PersistentLoginCookie(login, cookie.cookie(), cookie.data(), password);
	}
}
