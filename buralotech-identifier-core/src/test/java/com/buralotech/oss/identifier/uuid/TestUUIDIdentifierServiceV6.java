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
import com.buralotech.oss.identifier.api.IdentifierService;
import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.SecureRandom;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static com.buralotech.oss.identifier.uuid.TestData.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class TestUUIDIdentifierServiceV6 {

    private static final Random random = new SecureRandom();

    private final IdentifierService identifierService = new UUIDIdentifierService(new UUIDVersion6Delegate());

    @Test
    void generatedIdentifierIsConsistent() {
        assertThat(identifierService.generate())
                .satisfies(identifier -> {
                    assertThat(identifierService.fromBinary(identifier.binary())).isEqualTo(identifier);
                    assertThat(identifierService.fromText(identifier.text())).isEqualTo(identifier);
                });
    }

    @Test
    void generatedIdentifiersListIsConsistent() {
        assertThat(identifierService.generateList(17))
                .allSatisfy(identifier -> {
                    assertThat(identifierService.fromBinary(identifier.binary())).isEqualTo(identifier);
                    assertThat(identifierService.fromText(identifier.text())).isEqualTo(identifier);
                });
    }

    @Test
    void generatedIdentifiersStreamIsConsistent() {
        assertThat(identifierService.generateStream().limit(17))
                .allSatisfy(identifier -> {
                    assertThat(identifierService.fromBinary(identifier.binary())).isEqualTo(identifier);
                    assertThat(identifierService.fromText(identifier.text())).isEqualTo(identifier);
                });
    }

    @Test
    void identifiersAreSortable() {
        final var id1 = identifierService.generate();
        final var id2 = identifierService.generate();
        assertThat(id1).isLessThan(id2);
        assertThat(id2).isGreaterThan(id1);
    }

    @Test
    void identifierRepresentationsAreSortable() {
        final var id1 = identifierService.generate();
        final var id2 = identifierService.generate();
        assertThat(id1.text()).isLessThan(id2.text());
        assertThat(id2.text()).isGreaterThan(id1.text());
        assertThat(Arrays.compareUnsigned(id1.binary(), id2.binary())).isNegative();
        assertThat(Arrays.compareUnsigned(id2.binary(), id1.binary())).isPositive();
    }

    static Stream<Arguments> goodIdentifiers() {
        return Stream.of(
                arguments(GOOD_ID3_STR, GOOD_ID3_HEX, GOOD_ID3_BIN),
                arguments(GOOD_ID4_STR, GOOD_ID4_HEX, GOOD_ID4_BIN)
        );
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodTextRepresentations(final String text, final String hexString, final byte[] binary) {
        final var id = identifierService.fromText(text);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(id.text()).isEqualTo(text);
        assertThat(id.hex()).isEqualTo(hexString);
        assertThat(id.binary()).isEqualTo(binary);
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodBinaryRepresentations(final String text, final String hexString, final byte[] binary) {
        final var id = identifierService.fromBinary(binary);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(id.text()).isEqualTo(text);
        assertThat(id.hex()).isEqualTo(hexString);
        assertThat(id.binary()).isEqualTo(binary);
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodBinaryRepresentationsInByteArray(final String text, final String hexString, final byte[] binary) {
        final byte[] randomBinary = new byte[32];
        random.nextBytes(randomBinary);
        System.arraycopy(binary, 0, randomBinary, 8, 16);
        final var id = identifierService.fromBinary(randomBinary, 8);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(id.text()).isEqualTo(text);
        assertThat(id.hex()).isEqualTo(hexString);
        assertThat(id.binary()).isEqualTo(binary);
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodHexRepresentations(final String text, final String hexString, final byte[] binary) {
        final var id = identifierService.fromBinary(hexString);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(id.text()).isEqualTo(text);
        assertThat(id.hex()).isEqualTo(hexString);
        assertThat(id.binary()).isEqualTo(binary);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            "3TnesPWMmyqTHniq6DPq0",
            "3TnesPWMmyuTHniq6DPq0J",
            "ITnesPWMmyuTHniq6DPq0F",
            "3TnesPWMmyuTHniq6DPq0Fk"

    })
    void rejectBadTextualRepresentation(final String text) {
        assertThatThrownBy(() -> identifierService.fromText(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            "0",
            "xy",
            GOOD_ID1_HEX,
            GOOD_ID2_HEX,
            GOOD_ID5_HEX,
            GOOD_ID6_HEX
    })
    void rejectBadHexRepresentation(final String hexString) {
        assertThatThrownBy(() -> identifierService.fromBinary(hexString))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> rejectBadBinaryRepresentation() {
        return Stream.of(
                arguments((Object) new byte[0]),
                arguments((Object) new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74}),
                arguments((Object) new byte[]{33, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5}),
                arguments((Object) new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -34, 75, 59, -74, 28, -26, -74, 5}),
                arguments((Object) new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5, 5})
        );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource
    void rejectBadBinaryRepresentation(final byte[] binary) {
        assertThatThrownBy(() -> identifierService.fromBinary(binary))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> rejectBadBinaryRepresentationInTheMiddle() {
        return Stream.of(
                arguments(2, null),
                arguments(0, new byte[]{30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63}),
                arguments(1, new byte[]{30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63, 5}),
                arguments(4, new byte[]{0, 0, 0, 0, -2, -79, 33, -8, -95, 95, 79, -86, -79, 33, -8, -95, 95, 127, -86, 77, 0, 0, 0, 0}),
                arguments(4, new byte[]{0, 0, 0, 0, 1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15, 0, 0, 0, 0}));
    }

    @ParameterizedTest
    @MethodSource
    void rejectBadBinaryRepresentationInTheMiddle(final int offset,
                                                  final byte[] binary) {

        assertThatThrownBy(() -> identifierService.fromBinary(binary, offset))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void extractInstant() {
        final var identifier = identifierService.generate();
        assertThat(identifierService.toInstant(identifier)).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void extractNullInstant() {
        assertThat(identifierService.toInstant(null)).isNull();
    }

    static Stream<Arguments> checkInstant() {
        return Stream.of(
                arguments("6jWm4VZEQKPE_n58TPb00F", 1728576289556L),
                arguments("6jWm5mZ4PaXE_n58TPb00F", 1728576427125L),
                arguments("6jWm6G6XPIaE_n58TPb00F", 1728576478386L),
                arguments("6jWm6ZVpQjeE_n58TPb00F", 1728576510926L),
                arguments("6jWm6tEYPPiE_n58TPb00F", 1728576545705L),
                arguments("6jWm7IFjQTmE_n58TPb00F", 1728576589357L),
                arguments("6jWm7aeEOjqE_n58TPb00F", 1728576621902L)
        );
    }

    @ParameterizedTest
    @MethodSource
    void checkInstant(final String text, final long millis) {
        final var identifier = identifierService.fromText(text);
        final var instant = Instant.ofEpochMilli(millis);
        assertThat(identifierService.toInstant(identifier)).isCloseTo(instant, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void testInstantBounds() {
        final var now = Instant.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
    }

    @Test
    void testLocalDateBounds() {
        final var now = LocalDate.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
        assertThat(identifierService.toInstant(lower)).isCloseTo(LocalDateTime.of(now, LocalTime.of(0, 0, 0)).toInstant(ZoneOffset.UTC), within(1, ChronoUnit.SECONDS));
        assertThat(identifierService.toInstant(upper)).isCloseTo(LocalDateTime.of(now, LocalTime.of(23, 59, 59)).toInstant(ZoneOffset.UTC), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void testLocalDateTimeBounds() {
        final var now = LocalDateTime.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
        assertThat(identifierService.toInstant(lower)).isCloseTo(now.toInstant(ZoneOffset.UTC), within(1, ChronoUnit.SECONDS));
        assertThat(identifierService.toInstant(upper)).isCloseTo(now.toInstant(ZoneOffset.UTC), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void testOffsetDateTimeBounds() {
        final var now = OffsetDateTime.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
        assertThat(identifierService.toInstant(lower)).isCloseTo(now.toInstant(), within(1, ChronoUnit.SECONDS));
        assertThat(identifierService.toInstant(upper)).isCloseTo(now.toInstant(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void testZonedDateTimeBounds() {
        final var now = ZonedDateTime.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
        assertThat(identifierService.toInstant(lower)).isCloseTo(now.toInstant(), within(1, ChronoUnit.SECONDS));
        assertThat(identifierService.toInstant(upper)).isCloseTo(now.toInstant(), within(1, ChronoUnit.SECONDS));
    }

    private static Stream<Arguments> convertListOfIdentifiersToText() {
        return Stream.of(
                arguments(List.of(GOOD_ID3, GOOD_ID4), List.of(GOOD_ID3_STR, GOOD_ID4_STR)),
                arguments(List.of(), List.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertListOfIdentifiersToText(final List<Identifier> inputs,
                                        final List<String> expected) {
        assertThat(identifierService.toText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertListOfIdentifiersToBinary() {
        return Stream.of(
                arguments(List.of(GOOD_ID3, GOOD_ID4), List.of(GOOD_ID3_BIN, GOOD_ID4_BIN)),
                arguments(List.of(), List.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertListOfIdentifiersToBinary(final List<Identifier> inputs,
                                          final List<byte[]> expected) {
        assertThat(identifierService.toBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertListOfStringsToIdentifiers() {
        return Stream.of(
                arguments(List.of(GOOD_ID3_STR, GOOD_ID4_STR), List.of(GOOD_ID3, GOOD_ID4)),
                arguments(List.of(), List.of()),
                arguments(null, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertListOfStringsToIdentifiers(final List<String> inputs,
                                           final List<Identifier> expected) {
        assertThat(identifierService.fromText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertListOfByteArraysToIdentifiers() {
        return Stream.of(
                arguments(List.of(GOOD_ID3_BIN, GOOD_ID4_BIN), List.of(GOOD_ID3, GOOD_ID4)),
                arguments(List.of(), List.of()),
                arguments(null, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertListOfByteArraysToIdentifiers(final List<byte[]> inputs,
                                              final List<Identifier> expected) {
        assertThat(identifierService.fromBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfIdentifiersToText() {
        return Stream.of(
                arguments(Set.of(GOOD_ID3, GOOD_ID4), Set.of(GOOD_ID3_STR, GOOD_ID4_STR)),
                arguments(Set.of(), Set.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertSetOfIdentifiersToText(final Set<Identifier> inputs,
                                       final Set<String> expected) {
        assertThat(identifierService.toText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfIdentifiersToBinary() {
        return Stream.of(
                arguments(Set.of(GOOD_ID3, GOOD_ID4), Set.of(GOOD_ID3_BIN, GOOD_ID4_BIN)),
                arguments(Set.of(), Set.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertSetOfIdentifiersToBinary(final Set<Identifier> inputs,
                                         final Set<byte[]> expected) {
        assertThat(identifierService.toBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfStringsToIdentifiers() {
        return Stream.of(
                arguments(Set.of(GOOD_ID3_STR, GOOD_ID4_STR), Set.of(GOOD_ID3, GOOD_ID4)),
                arguments(Set.of(), Set.of()),
                arguments(null, Set.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertSetOfStringsToIdentifiers(final Set<String> inputs,
                                          final Set<Identifier> expected) {
        assertThat(identifierService.fromText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfByteArraysToIdentifiers() {
        return Stream.of(
                arguments(Set.of(GOOD_ID3_BIN, GOOD_ID4_BIN), Set.of(GOOD_ID3, GOOD_ID4)),
                arguments(Set.of(), Set.of()),
                arguments(null, Set.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertSetOfByteArraysToIdentifiers(final Set<byte[]> inputs,
                                             final Set<Identifier> expected) {
        assertThat(identifierService.fromBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertStringKeysInMapToIdentifiers() {
        final var obj1 = new Object();
        final var obj2 = new Object();
        return Stream.of(
                arguments(Map.of(GOOD_ID3_STR, obj1, GOOD_ID4_STR, obj2), Map.of(GOOD_ID3, obj1, GOOD_ID4, obj2)),
                arguments(Map.of(), Map.of()),
                arguments(null, Map.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertStringKeysInMapToIdentifiers(final Map<String, Object> inputs,
                                             final Map<Identifier, Object> expected) {
        assertThat(identifierService.fromText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertByteArrayKeysInMapToIdentifiers() {
        final var obj1 = new Object();
        final var obj2 = new Object();
        return Stream.of(
                arguments(Map.of(GOOD_ID3_BIN, obj1, GOOD_ID4_BIN, obj2), Map.of(GOOD_ID3, obj1, GOOD_ID4, obj2)),
                arguments(Map.of(), Map.of()),
                arguments(null, Map.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertByteArrayKeysInMapToIdentifiers(final Map<byte[], Object> inputs,
                                                final Map<Identifier, Object> expected) {
        assertThat(identifierService.fromBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertStringKeysInMapFromIdentifiers() {
        final var obj1 = new Object();
        final var obj2 = new Object();
        return Stream.of(
                arguments(Map.of(GOOD_ID3, obj1, GOOD_ID4, obj2), Map.of(GOOD_ID3_STR, obj1, GOOD_ID4_STR, obj2)),
                arguments(Map.of(), Map.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertStringKeysInMapFromIdentifiers(final Map<Identifier, Object> inputs,
                                               final Map<String, Object> expected) {
        assertThat(identifierService.toText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertByteArrayKeysInMapFromIdentifiers() {
        final var obj1 = new Object();
        final var obj2 = new Object();
        return Stream.of(
                arguments(Map.of(GOOD_ID3, obj1, GOOD_ID4, obj2), Map.of(GOOD_ID3_BIN, obj1, GOOD_ID4_BIN, obj2)),
                arguments(Map.of(), Map.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void convertByteArrayKeysInMapFromIdentifiers(final Map<Identifier, Object> inputs,
                                                  final Map<byte[], Object> expected) {
        assertThat(identifierService.toBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> canConvertUuidStringToIdentifier() {
        return Stream.of(
                arguments(null, null),
                arguments(GOOD_ID3_UUID_STR, GOOD_ID3),
                arguments(GOOD_ID4_UUID_STR, GOOD_ID4));
    }

    @ParameterizedTest
    @MethodSource
    void canConvertUuidStringToIdentifier(final String uuidString,
                                          final Identifier identifier) {
        assertThat(identifierService.fromUUID(uuidString)).isEqualTo(identifier);

    }

    private static Stream<Arguments> canConvertUuidToIdentifier() {
        return Stream.of(
                arguments(null, null),
                arguments(UUID.fromString(GOOD_ID3_UUID_STR), GOOD_ID3),
                arguments(UUID.fromString(GOOD_ID4_UUID_STR), GOOD_ID4));
    }

    @ParameterizedTest
    @MethodSource
    void canConvertUuidToIdentifier(final UUID uuid,
                                    final Identifier identifier) {
        assertThat(identifierService.fromUUID(uuid)).isEqualTo(identifier);
    }

    private static Stream<Arguments> cannotConvertInvalidUuidStringsToIdentifier() {
        return Stream.of(
                arguments(""),
                arguments(GOOD_ID3_STR),
                arguments(GOOD_ID4_STR),
                arguments(GOOD_ID3_HEX),
                arguments(GOOD_ID4_HEX),
                arguments(Generators.randomBasedGenerator().generate().toString()),
                arguments(Generators.timeBasedEpochRandomGenerator().generate().toString()));
    }

    @ParameterizedTest
    @MethodSource
    void cannotConvertInvalidUuidStringsToIdentifier(final String uuidString) {
        assertThatThrownBy(() -> identifierService.fromUUID(uuidString))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> cannotConvertInvalidUuidsToIdentifier() {
        return Stream.of(
                arguments(Generators.randomBasedGenerator().generate()),
                arguments(Generators.timeBasedEpochRandomGenerator().generate()));
    }

    @ParameterizedTest
    @MethodSource
    void cannotConvertInvalidUuidsToIdentifier(final UUID uuid) {
        assertThatThrownBy(() -> identifierService.fromUUID(uuid))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
