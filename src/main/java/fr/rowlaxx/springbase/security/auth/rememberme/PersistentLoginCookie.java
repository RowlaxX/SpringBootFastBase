package fr.rowlaxx.springbase.security.auth.rememberme;

import jakarta.servlet.http.Cookie;

public record PersistentLoginCookie(PersistentLogin login, Cookie cookie, String data, String dbPassword) {}