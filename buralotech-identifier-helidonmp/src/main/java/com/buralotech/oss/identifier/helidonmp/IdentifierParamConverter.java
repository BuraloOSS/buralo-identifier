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
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ext.ParamConverter;
import org.jspecify.annotations.Nullable;

/**
 * Used to bind/inject identifiers in their text representation as identifiers objects.
 */
public class IdentifierParamConverter implements ParamConverter<Identifier> {

    /**
     * The identifier service used for conversion from a string.
     */
    private final IdentifierService identifierService;

    /**
     * Initialize the parameter converter inserting the identifier service.
     *
     * @param identifierService The identifier service.
     */
    public IdentifierParamConverter(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    /**
     * Convert from a string value to an {@link Identifier}.
     *
     * @param value The string value.
     * @return The identifier.
     * @throws BadRequestException If the string value could not be converted.
     */
    @Override
    public Identifier fromString(@Nullable final String value) {
        try {
            if (value == null) {
                throw new IllegalArgumentException("identifiers cannot be null");
            }
            return identifierService.fromText(value);
        } catch (final IllegalArgumentException e) {
            throw new BadRequestException(e);
        }
    }

    /**
     * Convert from an {@link Identifier} to a string.
     *
     * @param value The {@link Identifier} value.
     * @return The string representation.
     */
    @Override
    public String toString(final Identifier value) {
        return value.text();
    }
}
