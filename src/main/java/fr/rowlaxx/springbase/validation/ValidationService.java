package fr.rowlaxx.springbase.validation;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ValidationService {
	private Validator validator;
	
	public <T> T tryValidate(T object) {
		var violations = validator.validate(object);
		if (violations.isEmpty())
			return object;
		throw new ConstraintViolationException(violations);
	}
}
