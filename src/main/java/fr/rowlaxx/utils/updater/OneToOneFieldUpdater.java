package fr.rowlaxx.utils.updater;

import java.lang.reflect.Field;

import fr.rowlaxx.springbase.jpa.BaseEntity;
import jakarta.persistence.OneToOne;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
class OneToOneFieldUpdater<ROOT extends BaseEntity, TYPE extends BaseEntity> extends RelationFieldUpdater<ROOT, TYPE, OneToOne> {
	
	public static OneToOneFieldUpdater<? extends BaseEntity, ? extends BaseEntity> from(Field field) {
		var oto = field.getAnnotation(OneToOne.class);
		if (oto == null)
			return null;
		if (oto.mappedBy().equals(""))
			return null;
		return new OneToOneFieldUpdater<>(field, oto);
	}
	
	private OneToOneFieldUpdater(Field field, OneToOne oto) {
		super(field, oto);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<TYPE> getTargetClass(Field field, OneToOne ano) {
		return ano.targetEntity() == void.class ? (Class<TYPE>) field.getType() : ano.targetEntity();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update(ROOT src, ROOT dst) throws IllegalAccessException {
		TYPE dstValue = (TYPE) field.get(dst);
		TYPE srcValue = (TYPE) field.get(src);
		log.info("-> " + field);
		
		if (dstValue == null && srcValue == null)
			return;
		else if (srcValue == null) {
			if (mapped != null)
				mapped.set(dstValue, null);
			field.set(dst, null);
		}
		else {
			if (dstValue == null) {
				dstValue = (TYPE) newInstance(srcValue.getClass());
				if (mapped != null)
					mapped.set(dstValue, dst);
				field.set(dst, dstValue);
			}
			EntityUpdater.update(srcValue, dstValue);
		}
	}

	

}
