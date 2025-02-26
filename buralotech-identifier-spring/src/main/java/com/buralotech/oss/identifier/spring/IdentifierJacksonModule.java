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

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson Module used to register the serializer and deserializers.
 */
public class IdentifierJacksonModule extends SimpleModule {

    /**
     * The constructor is used to create and register the serializer and deserializers.
     *
     * @param identifierService Required by the deserializers to convert string values to identifiers.
     */
    public IdentifierJacksonModule(final IdentifierService identifierService) {
        addSerializer(Identifier.class, new IdentifierSerializer());
        addDeserializer(Identifier.class, new IdentifierDeserializer(identifierService));
        addKeyDeserializer(Identifier.class, new IdentifierKeyDeserializer(identifierService));
    }
}
