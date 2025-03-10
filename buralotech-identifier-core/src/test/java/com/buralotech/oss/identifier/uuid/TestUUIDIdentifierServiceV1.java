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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestUUIDIdentifierServiceV1 {

    private static final String GOOD_ID1_STR = "3TnesPWMmyqTHniq6DPq0F";

    private static final String GOOD_ID2_STR = "3TnesPWMmyuTHniq6DPq0F";

    private static final byte[] GOOD_ID1_BIN = {17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5};

    private static final byte[] GOOD_ID2_BIN = {17, -20, -22, -31, -88, 87, -53, -18, -98, 75, 59, -74, 28, -26, -74, 5};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private final IdentifierService identifierService = new UUIDIdentifierService(new UUIDVersion1Delegate());

    @Test
    void generatedIdentifierIsConsistent() {
        assertThat(identifierService.generate())
                .satisfies(identifier -> {
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
        assertThat(Arrays.compare(id1.binary(), id2.binary())).isNegative();
        assertThat(Arrays.compare(id2.binary(), id1.binary())).isPositive();
    }

    static Stream<Arguments> goodIdentifiers() {
        return Stream.of(
                arguments(GOOD_ID1_STR, "11eceae1a857cbed9e4b3bb61ce6b605", GOOD_ID1_BIN),
                arguments(GOOD_ID2_STR, "11eceae1a857cbee9e4b3bb61ce6b605", GOOD_ID2_BIN)
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
            "xy"
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

    @Test
    void extractInstant() {
        final var identifier = identifierService.generate();
        assertThat(identifierService.toInstant(identifier)).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void extractNullInstant() {
        assertThat(identifierService.toInstant(null)).isNull();
    }

    @Test
    void extractInstantFromUnsupportedType() {
        assertThatThrownBy(() -> identifierService.toInstant(new Identifier() {
            @Override
            public String text() {
                return "";
            }

            @Override
            public byte[] binary() {
                return new byte[0];
            }
        })).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> checkInstant() {
        return Stream.of(
                arguments("3TpBKrC7Ku1PBn5tMoPz5-", 1665817399834L),
                arguments("3ThEOABeIM18pIEancdgaF", 1609857948615L),
                arguments("3ThEOBSVOw18pIEancdgaF", 1609857982524L),
                arguments("3ThEOBoj3Q18pIEancdgaF", 1609857992267L),
                arguments("3Th5zlgTiMuiZk81f0B--V", 1608934705053L),
                arguments("3Th5zlgTj21iZk81f0B--V", 1608934705053L),
                arguments("3Th5zlgTjH1iZk81f0B--V", 1608934705053L)
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
    public void testInstantBounds() {
        final var now = Instant.now();
        final var lower = identifierService.asLowerBound(now);
        final var upper = identifierService.asUpperBound(now);
        assertThat(lower.text()).isLessThan(upper.text());
        assertThat(upper.text()).isGreaterThan(lower.text());
        assertThat(lower).isLessThan(upper);
        assertThat(upper).isGreaterThan(lower);
    }

    @Test
    public void testLocalDateBounds() {
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
    public void testLocalDateTimeBounds() {
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
    public void testOffsetDateTimeBounds() {
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
    public void testZonedDateTimeBounds() {
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
                arguments(List.of(GOOD_ID1, GOOD_ID2), List.of(GOOD_ID1_STR, GOOD_ID2_STR)),
                arguments(List.of(), List.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void convertListOfIdentifiersToText(final List<Identifier> inputs,
                                               final List<String> expected) {
        assertThat(identifierService.toText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertListOfIdentifiersToBinary() {
        return Stream.of(
                arguments(List.of(GOOD_ID1, GOOD_ID2), List.of(GOOD_ID1_BIN, GOOD_ID2_BIN)),
                arguments(List.of(), List.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void convertListOfIdentifiersToBinary(final List<Identifier> inputs,
                                                 final List<byte[]> expected) {
        assertThat(identifierService.toBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertListOfStringsToIdentifiers() {
        return Stream.of(
                arguments(List.of(GOOD_ID1_STR, GOOD_ID2_STR), List.of(GOOD_ID1, GOOD_ID2)),
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
                arguments(List.of(GOOD_ID1_BIN, GOOD_ID2_BIN), List.of(GOOD_ID1, GOOD_ID2)),
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
                arguments(Set.of(GOOD_ID1, GOOD_ID2), Set.of(GOOD_ID1_STR, GOOD_ID2_STR)),
                arguments(Set.of(), Set.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void convertSetOfIdentifiersToText(final Set<Identifier> inputs,
                                              final Set<String> expected) {
        assertThat(identifierService.toText(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfIdentifiersToBinary() {
        return Stream.of(
                arguments(Set.of(GOOD_ID1, GOOD_ID2), Set.of(GOOD_ID1_BIN, GOOD_ID2_BIN)),
                arguments(Set.of(), Set.of()),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void convertSetOfIdentifiersToBinary(final Set<Identifier> inputs,
                                                final Set<byte[]> expected) {
        assertThat(identifierService.toBinary(inputs)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertSetOfStringsToIdentifiers() {
        return Stream.of(
                arguments(Set.of(GOOD_ID1_STR, GOOD_ID2_STR), Set.of(GOOD_ID1, GOOD_ID2)),
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
                arguments(Set.of(GOOD_ID1_BIN, GOOD_ID2_BIN), Set.of(GOOD_ID1, GOOD_ID2)),
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
                arguments(Map.of(GOOD_ID1_STR, obj1, GOOD_ID2_STR, obj2), Map.of(GOOD_ID1, obj1, GOOD_ID2, obj2)),
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
                arguments(Map.of(GOOD_ID1_BIN, obj1, GOOD_ID2_BIN, obj2), Map.of(GOOD_ID1, obj1, GOOD_ID2, obj2)),
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
                arguments(Map.of(GOOD_ID1, obj1, GOOD_ID2, obj2), Map.of(GOOD_ID1_STR, obj1, GOOD_ID2_STR, obj2)),
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
                arguments(Map.of(GOOD_ID1, obj1, GOOD_ID2, obj2), Map.of(GOOD_ID1_BIN, obj1, GOOD_ID2_BIN, obj2)),
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
}
