package fr.rowlaxx.utils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

import fr.rowlaxx.springbase.security.auth.token.UserAuthenticationToken;
import fr.rowlaxx.springbase.user.BaseUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Utils {
	private Utils() {}
	
	public static Optional<BaseUser> getCurrentUser() {
		var sc = SecurityContextHolder.getContext();
		var auth = sc.getAuthentication();
		
		if (auth instanceof UserAuthenticationToken userToken && auth.isAuthenticated())
			return Optional.of(userToken.getPrincipal());
		
		return Optional.empty();
	}
	
	public static Cookie extractCookie(HttpServletRequest request, String name) {
		var cookies = request.getCookies();
		if (cookies == null || cookies.length == 0)
			return null;
		
		for (var cookie : cookies)
			if (Objects.equals(cookie.getName(), name))
				return cookie;
		return null;
	}
	
	private static final SecureRandom RANDOM = new SecureRandom();
	public static byte[] generateRandomData(int length) {
		byte[] a = new byte[length];
		RANDOM.nextBytes(a);
		return a;
	}
	
	private static final Base64StringKeyGenerator BASE64_STRING_KEY_GENERATOR = new Base64StringKeyGenerator();
	public static String generateBase64SecretKey256Bits() {
		return BASE64_STRING_KEY_GENERATOR.generateKey();
	}
	
	public static String generateRandomDataHex(int length) {
		return HexUtils.toHexString(generateRandomData(length));
	}
	
	public static void clearCookie(HttpServletResponse response, String cookieName, String path) {
		var cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(path == null ? "/" : path);
		response.addCookie(cookie);
	}
	
	public static Duration substract(Instant i1, Instant i2) {
		long e1 = (i1.getEpochSecond() * 1_000_000_000l) + i1.getNano();
		long e2 = (i2.getEpochSecond() * 1_000_000_000l) + i2.getNano();
		return Duration.ofNanos(e1 - e2);
	}
}
