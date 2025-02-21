/*
 *  Copyright 2022-2025 Búraló Technologies
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
package com.buralotech.oss.identifier.api;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.HexFormat;

/**
 * Generate identifiers and parse binary and textual representations of identifiers.
 */
public interface IdentifierService {

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier.
     */
    Identifier generate();

    /**
     * Decode an identifier using its text representation.
     *
     * @param text The text representation.
     * @return The identifier.
     * @throws IllegalArgumentException If the text representation is not valid.
     */
    Identifier fromText(String text);

    /**
     * Decode an identifier using its binary representation.
     *
     * @param binary The binary representation.
     * @return The identifier.
     * @throws IllegalArgumentException If the binary representation is not valid.
     */
    Identifier fromBinary(byte[] binary);

    /**
     * Decode an identifier using its hexadecimal representation.
     *
     * @param hexString The hexadecimal representation.
     * @return The identifier.
     * @throws IllegalArgumentException If the binary representation is not valid.
     */
    default Identifier fromBinary(final String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException("invalid binary representation of identifier");
        }
        return fromBinary(HexFormat.of().parseHex(hexString));
    }

    /**
     * Extract an instant from an identifier.
     *
     * @param identifier The identifier.
     * @return The instant.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Instant toInstant(final Identifier identifier) {
        throw new UnsupportedOperationException("Cannot extract timestamp from " + identifier);
    }

    /**
     * Generate a lower-bound identifier for temporal value that can be used in range queries.
     *
     * @param time The temporal value ({@link java.time.Instant}, {@link java.time.LocalDate},
     *             {@link java.time.LocalDateTime}, {@link java.time.OffsetDateTime},
     *             {@link java.time.ZonedDateTime})
     * @return A lower-bound identifier that can be used in a range query.
     * @throws UnsupportedOperationException If the operation is not supported for the identifier type.
     * @throws IllegalArgumentException If the temporal type is not supported.
     */
    default Identifier asLowerBound(final Temporal time) {
        throw new UnsupportedOperationException("Cant generate lower-bound identifier for " + time);
    }

    /**
     * Generate an upper-bound identifier for temporal value that can be used in range queries.
     *
     * @param time The temporal value ({@link java.time.Instant}, {@link java.time.LocalDate},
     *             {@link java.time.LocalDateTime}, {@link java.time.OffsetDateTime},
     *             {@link java.time.ZonedDateTime})
     * @return A upper-bound identifier that can be used in a range query.
     * @throws UnsupportedOperationException If the operation is not supported for the identifier type.
     * @throws IllegalArgumentException If the temporal type is not supported.
     */
    default Identifier asUpperBound(final Temporal time) {
        throw new UnsupportedOperationException("Cant generate upper-bound identifier for " + time);
    }
}
