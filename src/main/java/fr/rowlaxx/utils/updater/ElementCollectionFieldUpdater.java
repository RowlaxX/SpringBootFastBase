package fr.rowlaxx.utils.updater;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.util.ReflectionUtils;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import jakarta.persistence.ElementCollection;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
class ElementCollectionFieldUpdater<ROOT extends BaseEntity> implements FieldUpdater<ROOT> {
	private final @NonNull Field field;

	public static ElementCollectionFieldUpdater<? extends BaseEntity> from(Field field) {
		var ec = field.getAnnotation(ElementCollection.class);
		if (ec == null)
			return null;
		return new ElementCollectionFieldUpdater<>(field);
	}
	
	public ElementCollectionFieldUpdater(Field field) {
		this.field = field;
		ReflectionUtils.makeAccessible(field);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
 	public void update(ROOT src, ROOT dst) throws IllegalAccessException {
		var srcValue = (Collection) field.get(src);
		var dstValue = (Collection) field.get(dst);
		log.debug(srcValue + " -> " + field);
		
		if (srcValue == null && dstValue == null)
			return;
		if (srcValue == null)
			field.set(dst, null);
		else {
			if (dstValue == null) {
				dstValue = new HashSet<>();
				field.set(dst, dstValue);
			}
			
			update(srcValue, dstValue);
		}
	}
	
	private <T> void update(Collection<T> srcCol, Collection<T> dstCol) {
		dstCol.removeIf(e -> !srcCol.contains(e));
		dstCol.addAll(srcCol);
	}
}
