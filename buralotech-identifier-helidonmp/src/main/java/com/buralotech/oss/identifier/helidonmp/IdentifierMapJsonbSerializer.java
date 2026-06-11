package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@Provider
public class IdentifierMapJsonbSerializer implements JsonbSerializer<Map<Identifier, Object>> {
    @Override
    public void serialize(@Nullable final Map<Identifier, Object> map,
                          final JsonGenerator generator,
                          final SerializationContext context) {
        if (map == null) {
            generator.writeNull();
        } else {
            generator.writeStartObject();
            map.forEach((key, value) -> {
                context.serialize(key.text(), value, generator);
            });
            generator.writeEnd();
        }
    }
}
