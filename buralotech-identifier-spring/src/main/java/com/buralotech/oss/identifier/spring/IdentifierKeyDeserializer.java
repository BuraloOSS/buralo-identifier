/*
 *  Copyright 2025 Búraló Technologies
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
package com.buralotech.oss.identifier.spring;

import com.buralotech.oss.identifier.api.IdentifierService;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;

/**
 * Deserializes map keys that are identifier types.
 */
public class IdentifierKeyDeserializer extends KeyDeserializer {

    /**
     * Used to create the identifier from its textual representation.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the deserializer.
     *
     * @param identifierService Used to create the identifier from its textual representation.
     */
    public IdentifierKeyDeserializer(final IdentifierService identifierService) {
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
