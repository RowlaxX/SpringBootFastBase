package fr.rowlaxx.springbase.security.auth.rememberme;

import java.util.UUID;

import jakarta.servlet.http.Cookie;

public record RawPersistentLoginCookie(UUID loginUuid, Cookie cookie, String data) {}
