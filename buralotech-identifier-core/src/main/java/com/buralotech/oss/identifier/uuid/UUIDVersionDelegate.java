/*
 * Copyright 2023-2026 Búraló Technologies
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buralotech.oss.identifier.uuid;

import java.time.Instant;

/**
 * Implementations encapsulate logic that is specific to the UUID format.
 */
public interface UUIDVersionDelegate {

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier as a byte array.
     */
    byte[] generate();

    /**
     * Check that the binary representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param binary The binary representation.
     * @param offset The offset of the binary representation.
     * @return {@code true} if the binary representation is valid. Otherwise, {@code false}.
     */
    boolean isValidBinary(byte[] binary, int offset);

    /**
     * Check that the text representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param text The binary representation.
     * @return {@code true} if the text representation is valid. Otherwise, {@code false}.
     */
    boolean isValidText(String text);

    /**
     * Extract the timestamp from the UUID.
     *
     * @param binary The binary representation of the UUID.
     * @return The timestamp as an Instant.
     */
    Instant toInstant(byte[] binary);

    /**
     * Create a UUID as a byte array from a timestamp.
     *
     * @param ticks  The timestamp in 100 nanoseconds.
     * @param suffix The second portion of the UUID.
     * @return The UUID as a byte array.
     */
    default byte[] fromTicks(long ticks, long suffix) {
        throw new UnsupportedOperationException();
    }
}
