package fr.rowlaxx.springbase.exception;

import org.springframework.core.NestedRuntimeException;

import lombok.experimental.StandardException;

@StandardException
public class InternalException extends NestedRuntimeException {
	private static final long serialVersionUID = 3676906225636570540L;
}
