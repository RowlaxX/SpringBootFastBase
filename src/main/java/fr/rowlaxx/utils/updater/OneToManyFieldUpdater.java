package fr.rowlaxx.utils.updater;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import jakarta.persistence.OneToMany;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
class OneToManyFieldUpdater<ROOT extends BaseEntity, TYPE extends BaseEntity> extends RelationFieldUpdater<ROOT, TYPE, OneToMany> {

	public static OneToManyFieldUpdater<? extends BaseEntity, ? extends BaseEntity> from(Field field) {
		var otm = field.getAnnotation(OneToMany.class);
		if (otm == null)
			return null;
		if (otm.mappedBy().equals(""))
			return null;
		return new OneToManyFieldUpdater<>(field, otm);
	}
	
	private OneToManyFieldUpdater(Field field, OneToMany ano) {
		super(field, ano);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<TYPE> getTargetClass(Field field, OneToMany ano) {
		var type = field.getGenericType();
		if (type instanceof Class<?> c && Collection.class.isAssignableFrom(c))
			return ano.targetEntity();
		else if (type instanceof ParameterizedType pt)
			return (Class<TYPE>) pt.getActualTypeArguments()[0];
		throw new IllegalStateException("Unknown one to many type");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update(ROOT src, ROOT dst) throws IllegalAccessException {
		var srcCol = (Collection<TYPE>) field.get(src);
		var dstCol = (Collection<TYPE>) field.get(dst);
		log.info("-> " + field);

		if (srcCol == null && dstCol == null)
			return;
		else if (srcCol == null) {
			for (var dstValue : dstCol)
				mapped.set(dstValue, null);
			field.set(dst, null);
		}
		else {
			if (dstCol == null) {
				dstCol = new HashSet<>();
				field.set(dst, dstCol);
			}
			
			removeOldsAndUnpersisted(srcCol, dstCol);
			updateOldsAndAddUnpersisted(srcCol, dstCol, dst);
		}
	}
	
	private void removeOldsAndUnpersisted(Collection<TYPE> srcCol, Collection<TYPE> dstCol) throws IllegalAccessException, IllegalArgumentException {
		var m2 = toMap(srcCol);
		var iterator = dstCol.iterator();
		TYPE value;
		UUID uuid;
		boolean remove;
		
		while(iterator.hasNext()) {
			value = iterator.next();
			remove = value == null;
			
			if (!remove) {
				uuid = value.getUuid();
				remove = uuid == null || !m2.containsKey(uuid);
			}
			
			if (remove) {
				if (mapped != null)
					mapped.set(value, null);
				iterator.remove();
			}
		}
	}
	
	private void updateOldsAndAddUnpersisted(Collection<TYPE> srcCol, Collection<TYPE> dstCol, ROOT dst) throws IllegalAccessException, IllegalArgumentException {
		var m1 = toMap(dstCol);
		TYPE dstValue;
		UUID uuid;
		
		for (var srcValue : srcCol)
			if ( (uuid = srcValue.getUuid()) == null || (dstValue = m1.get(uuid)) == null) {
				var cloned = clone(srcValue);
				mapped.set(cloned, dst);
				dstCol.add(cloned);
			}
			else
				EntityUpdater.update(srcValue, dstValue);
	}
	
	@SuppressWarnings("unchecked")
	private TYPE clone(TYPE another) throws IllegalAccessException, IllegalArgumentException {
		var i = (TYPE) newInstance(another.getClass());
		EntityUpdater.update(another, i);
		return i;
	}
	
	private Map<UUID, TYPE> toMap(Collection<TYPE> col) {
		return col.stream()
				.filter(be -> be.getUuid() != null)
				.collect(Collectors.toMap(BaseEntity::getUuid, Function.identity()));
	}
	

	

}
