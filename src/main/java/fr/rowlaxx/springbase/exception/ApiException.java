package fr.rowlaxx.springbase.exception;

import org.springframework.core.NestedRuntimeException;

import lombok.experimental.StandardException;

@StandardException
public class ApiException extends NestedRuntimeException {
	private static final long serialVersionUID = 8097148412307220586L;
}
