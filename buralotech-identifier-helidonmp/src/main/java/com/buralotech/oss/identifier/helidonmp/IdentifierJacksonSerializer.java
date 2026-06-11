/*
 *  Copyright 2026 Búraló Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serialize an {@link Identifier} value to JSON.
 */
public class IdentifierJacksonSerializer extends StdSerializer<Identifier> {

    /**
     * Default constructor.
     */
    public IdentifierJacksonSerializer() {
        this(Identifier.class);
    }

    /**
     * Parameterized constructor.
     *
     * @param clazz The class for the identifier type.
     */
    public IdentifierJacksonSerializer(final Class<Identifier> clazz) {
        super(clazz);
    }

    /**
     * Serialize a non-null {@link Identifier value} as a JSON string.
     *
     * @param identifier    The identifier value to serialize.
     * @param jsonGenerator Used to output the JSON.
     * @param provider      Ignored.
     */
    @Override
    public void serialize(final Identifier identifier,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider provider)
            throws IOException {
        jsonGenerator.writeString(identifier.text());
    }
}
