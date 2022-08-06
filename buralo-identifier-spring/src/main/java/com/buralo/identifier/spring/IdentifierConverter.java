/*
 *  Copyright 2022 Búraló Technologies
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
package com.buralo.identifier.spring;

import com.buralo.identifier.api.Identifier;
import com.buralo.identifier.api.IdentifierService;
import org.springframework.core.convert.converter.Converter;

/**
 * Used to bind/inject identifiers in their text representation as identifiers objects.
 */
public class IdentifierConverter implements Converter<String, Identifier> {

    /**
     * The identifier service.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the converter.
     *
     * @param identifierService The identifier service.
     */
    public IdentifierConverter(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    /**
     * Perform the conversion by delegating to the identifier service.
     *
     * @param source The textual representation of the identifier.
     * @return The identifier object.
     */
    @Override
    public Identifier convert(final String source) {
        return identifierService.fromText(source);
    }
}