package fr.rowlaxx.utils.updater;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import fr.rowlaxx.springbase.jpa.BaseEntity;
import lombok.NonNull;

import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.findField;

abstract class RelationFieldUpdater<ROOT extends BaseEntity, TYPE extends BaseEntity, ANO> implements FieldUpdater<ROOT> {
	protected final @NonNull Field field;
	protected final @NonNull Field mapped;
	
	RelationFieldUpdater(Field field, ANO ano){
		this.field = field;
		makeAccessible(field);
		
		var target = getTargetClass(field, ano);
		if (!BaseEntity.class.isAssignableFrom(target))
			throw new IllegalArgumentException("target type must be a child class of base entity");
		
		this.mapped = findField(target, getMappedBy(ano));
		makeAccessible(mapped);
	}
	
	protected abstract Class<TYPE> getTargetClass(Field field, ANO ano);
	
	private String getMappedBy(ANO ano) {
		try {
			var method = ano.getClass().getDeclaredMethod("mappedBy");
			return (String) method.invoke(ano);
		}catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected <T extends BaseEntity> T newInstance(Class<T> type) throws IllegalAccessException, IllegalArgumentException {
		try {
			return type.getConstructor().newInstance();
		}catch(NoSuchMethodException | InvocationTargetException | InstantiationException e) {
			throw new IllegalStateException(e);
		}
	}
}
