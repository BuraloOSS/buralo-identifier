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
package com.buralotech.oss.identifier.micronaut;

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class IdentifierConfig {

    /**
     * Produces the identifier service bean.
     *
     * @param properties The configuration properties used to specify the identifier type.
     * @return An identifier service that generates UUID-based identifiers.
     */
    @Bean
    @Singleton
    public IdentifierService identifierService(final IdentifierConfigProperties properties) {
        return UUIDIdentifierService.forVersion(properties.generator());
    }

    /**
     * Conversion support for Identifiers.
     *
     * @param identifierService The identifier service.
     * @return The converter bean.
     */
    @Bean
    @Singleton
    public IdentifierConverter identifierConverter(final IdentifierService identifierService) {
        return new IdentifierConverter(identifierService);
    }

    /**
     * Serialization/deserialization support for identifiers.
     *
     * @param identifierService The identifier service.
     * @return The serialization/deserialization support.
     */
    @Bean
    @Singleton
    public IdentifierSerde identifierSerde(final IdentifierService identifierService) {
        return new IdentifierSerde(identifierService);
    }
}
