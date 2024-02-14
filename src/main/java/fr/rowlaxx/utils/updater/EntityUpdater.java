package fr.rowlaxx.utils.updater;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import jakarta.persistence.ManyToMany;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityUpdater {
	private static final FieldFilter ENTITY_FILTER = field -> {
		var m = field.getModifiers();
		if (Modifier.isStatic(m) || Modifier.isFinal(m) || Modifier.isTransient(m))
			return false;
		
		var dc = field.getDeclaringClass();
		if (dc.isAssignableFrom(BaseEntity.class))
			return false;
		
		if (field.isAnnotationPresent(UpdateIgnore.class))
			return false;
		
		if (field.isAnnotationPresent(ManyToMany.class))
			throw new UnsupportedOperationException("ManyToMany not supported yet");
		
		return !BaseEntity.class.isAssignableFrom(field.getType());
	};
	
	@SuppressWarnings("rawtypes")
	private final Map<Class<?>, List> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> void update(T src, T dst) {
		var clazz = (Class<T>) src.getClass();
		if (clazz != dst.getClass())
			throw new IllegalStateException("both entities must have the same class");
		
		var updaters = (List<FieldUpdater<T>>) cache.computeIfAbsent(clazz, c -> genFields(clazz));

		try {
			for (var updater : updaters)
				updater.update(src, dst);
		}catch(IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}	
	
	@SuppressWarnings("unchecked")
	private <T extends BaseEntity> List<FieldUpdater<T>> genFields(Class<T> clazz) {
		var list = new LinkedList<FieldUpdater<T>>();
		
		ReflectionUtils.doWithFields(clazz, field -> {
			Object fu = OneToOneFieldUpdater.from(field);
			if (fu == null)
				fu = OneToManyFieldUpdater.from(field);
			if (fu == null)
				fu = ElementCollectionFieldUpdater.from(field);
			if (fu == null)
				fu = new ClassicFieldUpdater<>(field);
			
			list.add((FieldUpdater<T>)fu);
		}, ENTITY_FILTER);
		
		return list;
	}
}
