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

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Converts strings used as request parameters to Identifiers.
 */
public class IdentifierConverter implements TypeConverter<String, Identifier> {

    /**
     * The identifier service.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the converter with the identifier service.
     *
     * @param identifierService The identifier service.
     */
    public IdentifierConverter(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    /**
     * Perform the conversion.
     *
     * @param value      The input string.
     * @param targetType The target type (i.e. {@code Identifier})
     * @param context    The conversion context (ignored)
     * @return An optional containing the converted value or empty if the conversion cannot be performed.
     */
    @Override
    public Optional<Identifier> convert(@Nullable final String value,
                                        final Class<Identifier> targetType,
                                        final ConversionContext context) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(identifierService.fromText(value));
        } catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
