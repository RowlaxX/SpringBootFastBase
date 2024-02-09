package fr.rowlaxx.springbase.exception;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
	private static final Pattern REDIRECT_PATTERN = Pattern.compile("^\\{[\\w\\/\\.\\-]*\\}");

	private String[] extractRedirect(String cause) {
		var result = new String[2];
		var splitted = REDIRECT_PATTERN.split(cause, 2);
		
		if (splitted.length == 1)
			result[1] = splitted[0];
		else {
			result[0] = cause
					.substring(1, cause.length() - splitted[1].length() - 1)
					.trim();
			result[1] = splitted[1]
					.trim();
		}
			
		return result;
	}
	
	private <T> ResponseEntity<T> response(HttpStatus status, Object cause) {
		var pd = ProblemDetail.forStatus(status);
		pd.setProperty("timestamp", System.currentTimeMillis());
		
		URI location = null;
		
		if (cause != null && cause instanceof String causeStr) {
			var result = extractRedirect(causeStr);
			if (result[0] != null) {
				location = URI.create(result[0]);
				cause = result[1];
			}
		}
		
		pd.setProperty("cause", cause);

		var re = ResponseEntity.of(pd);
		if (location != null)
			re.location(location);
		return re.build();
	}
	
	
	
	/*
	 * 302 Found
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(RedirectException exception){
		return ResponseEntity.status(FOUND).location(URI.create(exception.getPath())).build();
	}
	
	
	
	
	
	/*
	 * 400 Bad Request
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(MethodArgumentNotValidException exception) {
		String field;
		String[] codes;
		String cause;
		String[] path;
		List<String> list;
		var errors = exception.getAllErrors();
		var details = new HashMap<String, List<String>>();
		
		for (var error : errors) {
			codes = error.getCodes();
			field = codes == null ? "_" : codes[0];
			path = field.split("\\.");
			field = path[path.length - 1];
			cause = error.getDefaultMessage();
			list = details.computeIfAbsent(field, f -> new LinkedList<>());
			list.add(cause);
		}
		
		return response(BAD_REQUEST, details);
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(ConstraintViolationException exception) {
		var details = new HashMap<String, List<String>>();
		String field;
		String cause;
		List<String> list;

		for (var violation : exception.getConstraintViolations()) {
			field = violation.getPropertyPath().toString();
			cause = violation.getMessage();
			list = details.computeIfAbsent(field, f -> new LinkedList<>());
			list.add(cause);
		}
		
		return response(BAD_REQUEST, details);
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(HttpMessageNotReadableException exception) {
		var msg = exception.getMessage();
		if (msg.startsWith("Required request body is missing: "))
			msg = "Required request body is missing";
		if (msg.startsWith("JSON parse error: "))
			msg = "JSON parse error";
		
		return response(BAD_REQUEST, msg);
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(HttpMediaTypeException exception) {
		return response(BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(ApiException exception) {
		return response(BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(AuthenticationException exception) {
		return response(BAD_REQUEST, exception.getMessage());
	}
	
	
	/*
	 * 403 Forbidden
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(AccessDeniedException exception) {
		return response(FORBIDDEN, exception.getMessage());
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(CsrfException exception){ 
		return response(FORBIDDEN, exception.getMessage()); 
	}
	
	
	
	
	
	
	
	/*
	 * 404 Not found
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(NoResourceFoundException exception) {
		return response(NOT_FOUND, exception.getMessage());
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(NoHandlerFoundException exception) {
		return response(NOT_FOUND, exception.getMessage());
	}
	
	
	
	
	
	
	
	/*
	 * 405 Method not allowed
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(HttpRequestMethodNotSupportedException exception){
		return response(METHOD_NOT_ALLOWED, exception.getMessage());
	}
	
	
	
	
	
	
	/*
	 * 500 Internal Server Error
	 */
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(DataAccessException exception) {
		exception.printStackTrace();
		return response(INTERNAL_SERVER_ERROR, "A database error occured");
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(InternalException exception) {
		return response(INTERNAL_SERVER_ERROR, exception.getMessage());
	}
	
	@ExceptionHandler
	public <T> ResponseEntity<T> handle(Exception exception) {
		exception.printStackTrace();
		return response(INTERNAL_SERVER_ERROR, "Unknown error");
	}
}
