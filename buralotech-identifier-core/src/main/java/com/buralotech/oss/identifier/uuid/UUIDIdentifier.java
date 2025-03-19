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
package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.Identifier;
import com.fasterxml.uuid.impl.UUIDUtil;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;

/**
 * And identifier has a textual and binary representation.
 *
 * @param text   The textual representation.
 * @param binary The binary representation.
 */
public record UUIDIdentifier(String text, byte[] binary) implements Identifier {


    /**
     * Alternative constructor that initialises the binary representation by parsing a hexadecimal string.
     *
     * @param text The textual representation.
     * @param hex  The hexadecimal representation of the binary.
     */
    public UUIDIdentifier(String text, String hex) {
        this(text, HexFormat.of().parseHex(hex));
    }

    /**
     * Get the identifier as a UUID.
     *
     * @return The UUID.
     */
    @Override
    public  UUID uuid() {
        return UUIDUtil.uuid(binary());
    }

    /**
     * Get the identifier as a UUID string.
     *
     * @return The UUID string.
     */
    @Override
    public String uuidString() {
        return uuid().toString();
    }

    /**
     * Determine if two identifiers are equivalent.
     *
     * @param other The other identifier.
     * @return {@code true} if the two identifiers are equivalent. Otherwise, {@code false}.
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return switch (other) {
            case UUIDIdentifier that -> Arrays.equals(binary, that.binary);
            case byte[] that -> Arrays.equals(binary, that);
            case String that -> text.equals(that);
            default ->  false;
        };
    }

    /**
     * Calculate a hash code for an identifier.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(binary);
    }

    /**
     * Get a string representation.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return text;
    }
}
