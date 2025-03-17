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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

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
     * Generate a list of identifiers using an underlying UUID generator.
     *
     * @param n The number of identifiers to generate.
     * @return The list of identifiers.
     */
    default List<Identifier> generate(int n) {
        return Stream.generate(this::generate).limit(n).toList();
    }

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
     * Create an identifier from a UUID string.
     *
     * @param uuid The UUID string.
     * @return The identifier.
     */
    default Identifier fromUUID(final String uuid) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create an identifier from a UUID.
     *
     * @param uuid The UUID.
     * @return The identifier.
     */
    default Identifier fromUUID(UUID uuid) {
        throw new UnsupportedOperationException();
    }

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
     * @throws IllegalArgumentException      If the temporal type is not supported.
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
     * @throws IllegalArgumentException      If the temporal type is not supported.
     */
    default Identifier asUpperBound(final Temporal time) {
        throw new UnsupportedOperationException("Cant generate upper-bound identifier for " + time);
    }

    /**
     * Decode a list of identifier using text representations.
     *
     * @param list The text representations.
     * @return The identifiers.
     * @throws IllegalArgumentException If any of the input's text representation is not valid.
     */
    default List<Identifier> fromText(final List<String> list) {
        return list == null
                ? List.of()
                : list.stream().map(this::fromText).toList();
    }

    /**
     * Decode a list of identifier using binary representations.
     *
     * @param list The binary representations.
     * @return The identifiers.
     * @throws IllegalArgumentException If any of the input's binary representation is not valid.
     */
    default List<Identifier> fromBinary(final List<byte[]> list) {
        return list == null
                ? List.of()
                : list.stream().map(this::fromBinary).toList();
    }

    /**
     * Get the text representation for a list of identifiers.
     *
     * @param list The list of identifiers.
     * @return The text representation of the identifiers.
     */
    default List<String> toText(final List<Identifier> list) {
        return list == null
                ? null
                : list.stream().map(Identifier::text).toList();
    }

    /**
     * Get the binary representation for a list of identifiers.
     *
     * @param list The list of identifiers.
     * @return The binary representation of the identifiers.
     */
    default List<byte[]> toBinary(final List<Identifier> list) {
        return list == null
                ? null
                : list.stream().map(Identifier::binary).toList();
    }

    /**
     * Decode a set of identifier using text representations.
     *
     * @param set The text representations.
     * @return The identifiers.
     * @throws IllegalArgumentException If any of the input's text representation is not valid.
     */
    default Set<Identifier> fromText(final Set<String> set) {
        return set == null
                ? Set.of()
                : set.stream().map(this::fromText).collect(toUnmodifiableSet());
    }

    /**
     * Decode a set of identifier using binary representations.
     *
     * @param set The binary representations.
     * @return The identifiers.
     * @throws IllegalArgumentException If any of the input's binary representation is not valid.
     */
    default Set<Identifier> fromBinary(final Set<byte[]> set) {
        return set == null
                ? Set.of()
                : set.stream().map(this::fromBinary).collect(toUnmodifiableSet());
    }

    /**
     * Get the text representation for a set of identifiers.
     *
     * @param set The list of identifiers.
     * @return The text representation of the identifiers.
     */
    default Set<String> toText(final Set<Identifier> set) {
        return set == null
                ? null
                : set.stream().map(Identifier::text).collect(toUnmodifiableSet());
    }

    /**
     * Get the binary representation for a set of identifiers.
     *
     * @param set The list of identifiers.
     * @return The binary representation of the identifiers.
     */
    default Set<byte[]> toBinary(final Set<Identifier> set) {
        return set == null
                ? null
                : set.stream().map(Identifier::binary).collect(toUnmodifiableSet());
    }

    /**
     * Decode a list of identifier using text representations.
     *
     * @param map The map keyed by text representations.
     * @return The map keyed by identifiers.
     * @throws IllegalArgumentException If any of the input's text representation is not valid.
     */
    default <T> Map<Identifier, T> fromText(final Map<String, T> map) {
        return map == null
                ? Map.of()
                : map.entrySet().stream().collect(toUnmodifiableMap(e -> fromText(e.getKey()), Map.Entry::getValue));
    }

    /**
     * Decode a list of identifier using binary representations.
     *
     * @param map The map keyed by binary representations.
     * @return The map keyed by identifiers.
     * @throws IllegalArgumentException If any of the input's binary representation is not valid.
     */
    default <T> Map<Identifier, T> fromBinary(final Map<byte[], T> map) {
        return map == null
                ? Map.of()
                : map.entrySet().stream().collect(toUnmodifiableMap(e -> fromBinary(e.getKey()), Map.Entry::getValue));
    }

    /**
     * Convert the keys of a map from identifiers to their text representation.
     *
     * @param map A map keyed by identifiers.
     * @return The map keyed by the text representation of the identifier.
     */
    default <T> Map<String, T> toText(final Map<Identifier, T> map) {
        return map == null
                ? null
                : map.entrySet().stream().collect(toUnmodifiableMap(e -> e.getKey().text(), Map.Entry::getValue));
    }

    /**
     * Convert the keys of a map from identifiers to their binary representation.
     *
     * @param map A map keyed by identifiers.
     * @return The map keyed by the binary representation of the identifier.
     */
    default <T> Map<byte[], T> toBinary(final Map<Identifier, T> map) {
        return map == null
                ? null
                : map.entrySet().stream().collect(toUnmodifiableMap(e -> e.getKey().binary(), Map.Entry::getValue));
    }
}
