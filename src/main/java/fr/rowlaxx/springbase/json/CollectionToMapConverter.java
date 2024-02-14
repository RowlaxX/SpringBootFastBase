package fr.rowlaxx.springbase.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.util.StdConverter;

import fr.rowlaxx.springbase.jpa.BaseEntity;

public class CollectionToMapConverter<T extends BaseEntity> extends StdConverter<List<T>, Map<UUID, T>> {

	@Override
	public Map<UUID, T> convert(List<T> values) {
		Map<UUID, T> map = new HashMap<>(values.size());
		values.forEach(v -> map.put(v.getUuid(), v));
		return map;
	}
}
