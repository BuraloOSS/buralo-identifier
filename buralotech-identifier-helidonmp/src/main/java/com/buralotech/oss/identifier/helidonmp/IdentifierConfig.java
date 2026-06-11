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

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Factory that exports beans provided by the {@code buralotech-identifier-helidonmp} module.
 */
@ApplicationScoped
public class IdentifierConfig {

    /**
     * The identifier service bean is used to generate unique identifiers.
     *
     * @param generator The version of the underlying UUID. (One of: {@code "v7"}, {@code "v6"}, or {@code "v4"})
     * @return The identifier service.
     */
    @Produces
    @ApplicationScoped
    public IdentifierService identifierService(@ConfigProperty(name = "buralotech.identifier.generator", defaultValue = "v7") String generator) {
        return UUIDIdentifierService.forVersion(generator);
    }
}
