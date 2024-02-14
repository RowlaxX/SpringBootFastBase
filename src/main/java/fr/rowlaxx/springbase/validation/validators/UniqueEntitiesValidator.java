package fr.rowlaxx.springbase.validation.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import fr.rowlaxx.springbase.validation.constraints.UniqueEntities;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEntitiesValidator implements ConstraintValidator<UniqueEntities, Collection<? extends BaseEntity>> {

	@Override
	public boolean isValid(Collection<? extends BaseEntity> value, ConstraintValidatorContext context) {
		var uuids = new HashSet<UUID>();
		UUID uuid;
		
		for (var entity : value)
			if ((uuid = entity.getUuid()) != null && !uuids.add(uuid))
					return false;
		return true;
	}

}
