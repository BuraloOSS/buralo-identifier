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

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Configure beans exported by the library.
 */
@ApplicationScoped
public class IdentifierConfig {

    /**
     * Create the identifier service bean.
     *
     * @return The identifier service bean.
     */
    @ApplicationScoped
    @Produces
    IdentifierService identifierService(final IdentifierConfigProperties properties) {
        return UUIDIdentifierService.forVersion(properties.generator().orElse(null));
    }

    /**
     * Create a customizer to register a Jackson module that serializes/deserializes identifiers.
     *
     * @param identifierService The identifier service.
     * @return The customizer.
     */
    @ApplicationScoped
    @Produces
    ObjectMapperCustomizer identifierJacksonCustomizer(final IdentifierService identifierService) {
        return objectMapper -> objectMapper.registerModule(new IdentifierJacksonModule(identifierService));
    }
}
