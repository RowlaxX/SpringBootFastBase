package fr.rowlaxx.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class JobTable<T> {
	private final Map<T, ReentrantLock> map = new ConcurrentHashMap<>();
	
	public void use(T object) {
		var lock = map.computeIfAbsent(object, o -> new ReentrantLock());
		lock.lock();
	}
	
	public void free(T object) {
		var lock = map.get(object);
		if (lock == null)
			throw new IllegalStateException("There is no such hold for " + object);
		
		lock.unlock();
		if (!lock.isLocked())
			map.remove(object);
	}
}
