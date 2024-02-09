package fr.rowlaxx.springbase.validation.validators;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import fr.rowlaxx.springbase.validation.constraints.RawPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RawPasswordValidator implements ConstraintValidator<RawPassword, String> {
	private static final Pattern PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
	private static final Predicate<String> PREDICATE = PATTERN.asMatchPredicate();
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		return PREDICATE.test(value);
	}
}
