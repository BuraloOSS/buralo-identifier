package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.Nullable;

@Provider
public class IdentifierJsonbSerializer implements JsonbSerializer<Identifier> {
    @Override
    public void serialize(@Nullable final Identifier value,
                          final JsonGenerator generator,
                          final SerializationContext context) {
        if (value == null) {
            generator.writeNull();
        } else {
            generator.write(value.text());
        }
    }
}
