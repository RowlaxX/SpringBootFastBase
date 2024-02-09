package fr.rowlaxx.springbase.exception;

import java.util.Objects;

import org.springframework.core.NestedRuntimeException;

import lombok.Getter;

public class RedirectException extends NestedRuntimeException {
	private static final long serialVersionUID = -5887442171765804748L;
	
	@Getter
	private final String path;
	
	public RedirectException(String path) {
		super("Redirecting");
		this.path = Objects.requireNonNull(path);
	}
}
