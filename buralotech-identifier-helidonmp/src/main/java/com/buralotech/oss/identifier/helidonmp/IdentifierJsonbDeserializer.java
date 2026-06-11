package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

@Provider
public class IdentifierJsonbDeserializer implements JsonbDeserializer<Identifier> {

    private final IdentifierService identifierService;

    public IdentifierJsonbDeserializer(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public @Nullable Identifier deserialize(final JsonParser parser,
                                            final DeserializationContext context,
                                            final Type type) {
        if (!parser.hasNext() || parser.next() != JsonParser.Event.VALUE_STRING) {
            return null;
        }

        final var value = parser.getString();
        if (value == null) {
            return null;
        }

        return identifierService.fromText(value);
    }
}
