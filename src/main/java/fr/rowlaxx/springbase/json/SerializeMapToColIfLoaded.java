package fr.rowlaxx.springbase.json;

import java.io.IOException;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentMap;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SerializeMapToColIfLoaded extends JsonSerializer<Map<?, ?>> {

	@Override
	public void serialize(Map<?,?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value instanceof PersistentMap) {
			if (Hibernate.isInitialized(value))
				gen.writeObject(value.values());
			else
				gen.writeNull();
		}
		else {
			gen.writeObject(value.values());
		}
	}
	
}
