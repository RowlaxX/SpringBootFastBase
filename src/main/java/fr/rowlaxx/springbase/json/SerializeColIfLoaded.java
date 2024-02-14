package fr.rowlaxx.springbase.json;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SerializeColIfLoaded extends JsonSerializer<Collection<?>> {

	@Override
	public void serialize(Collection<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value instanceof PersistentCollection) {
			if (Hibernate.isInitialized(value))
				gen.writeObject(value);
			else
				gen.writeNull();
		}
		else {
			gen.writeObject(value);
		}
	}

}
