package fr.rowlaxx.utils.updater;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
class ClassicFieldUpdater<ROOT extends BaseEntity> implements FieldUpdater<ROOT> {
	private final @NonNull Field field;

	public ClassicFieldUpdater(Field field) {
		this.field = field;
		ReflectionUtils.makeAccessible(field);
	}
	
	@Override
	public void update(ROOT src, ROOT dst) throws IllegalAccessException {
		var value = field.get(src);
		log.debug(value + " -> " + field);
		field.set(dst, value);
	}
}
