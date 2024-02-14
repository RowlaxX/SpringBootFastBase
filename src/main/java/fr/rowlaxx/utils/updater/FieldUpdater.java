package fr.rowlaxx.utils.updater;

import fr.rowlaxx.springbase.jpa.BaseEntity;

interface FieldUpdater<T extends BaseEntity> {
	public void update(T src, T dst) throws IllegalAccessException;
}
