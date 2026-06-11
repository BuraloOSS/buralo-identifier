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
import com.buralotech.oss.identifier.api.IdentifierService;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

/**
 * Deserializes map keys that are identifier types.
 */
public class IdentifierJacksonKeyDeserializer extends StdKeyDeserializer {

    /**
     * Used to create the identifier from its textual representation.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the deserializer.
     *
     * @param identifierService Used to create the identifier from its textual representation.
     */
    public IdentifierJacksonKeyDeserializer(final IdentifierService identifierService) {
        super(-1, Identifier.class);
        this.identifierService = identifierService;
    }

    /**
     * Deserialize a map key as an identifier.
     *
     * @param key  The map key as string.
     * @param ctxt The deserialization context. (ignored)
     * @return The identifier.
     */
    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) {
        return identifierService.fromText(key);
    }
}
