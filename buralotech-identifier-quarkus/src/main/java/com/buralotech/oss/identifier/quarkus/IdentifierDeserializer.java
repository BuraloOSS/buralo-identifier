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
package com.buralotech.oss.identifier.quarkus;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Deserializes map keys that are identifier types.
 */
public class IdentifierDeserializer extends StdDeserializer<Identifier> {

    /**
     * Used to create the identifier from its textual representation.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the deserializer.
     *
     * @param identifierService Used to create the identifier from its textual representation.
     */
    public IdentifierDeserializer(final IdentifierService identifierService) {
        super(Identifier.class);
        this.identifierService = identifierService;
    }

    /**
     * Deserializes the JSON string containing the textual representation of an identifier.
     *
     * @param jsonParser Parser used for reading JSON content.
     * @param ctxt       The deserialization context (ignored).
     * @return The identifier.
     */
    @Override
    public Identifier deserialize(final JsonParser jsonParser,
                                  final DeserializationContext ctxt)
            throws IOException {
        final var text = jsonParser.readValueAs(String.class);
        try {
            return identifierService.fromText(text);
        } catch (final IllegalArgumentException e) {
            throw new JsonMappingException(jsonParser, "Could not deserialize identifier", e);
        }
    }
}
