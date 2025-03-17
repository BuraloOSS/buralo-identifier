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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestUUIDIdentifierServiceV4 {

    private static final String GOOD_ID1_STR = "zf3Wy94UIuel7UXWMryeIF";

    private static final String GOOD_ID2_STR = "l0bK5MCbHUmOpG_nOkbw4k";

    private static final String GOOD_ID1_HEX = "feb121f8a15f4faab121f8a15f7faa4d";

    private static final String GOOD_ID2_HEX = "c419d519736749fc99d519736709fc17";

    private static final byte[] GOOD_ID1_BIN = {-2, -79, 33, -8, -95, 95, 79, -86, -79, 33, -8, -95, 95, 127, -86, 77};

    private static final byte[] GOOD_ID2_BIN = {-60, 25, -43, 25, 115, 103, 73, -4, -103, -43, 25, 115, 103, 9, -4, 23};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private final IdentifierService identifierService = new UUIDIdentifierService(new UUIDVersion4Delegate());

    @Test
    void generatedIdentifierIsConsistent() {
        var id = identifierService.generate();
        System.out.println(id.text());
        System.out.println(id.hex());
        for (int i = 0; i < 16; i++) {
            if (i != 0) {
                System.out.print(",");
            }
            System.out.print(id.binary()[i]);
        }
        System.out.println();
        assertThat(identifierService.generate())
                .satisfies(identifier -> {
                    assertThat(identifierService.fromBinary(identifier.binary())).isEqualTo(identifier);
                    assertThat(identifierService.fromText(identifier.text())).isEqualTo(identifier);
                });
    }


    static Stream<Arguments> goodIdentifiers() {
        return Stream.of(
                arguments(GOOD_ID1_STR, GOOD_ID1_HEX, GOOD_ID1_BIN),
                arguments(GOOD_ID2_STR, GOOD_ID2_HEX, GOOD_ID2_BIN)
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
            "xy",
            "11eceae1a857cbed9e4b3bb61ce6b605",
            "1ef8720e9e5860e48f97318979a9c105"
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
                arguments((Object) new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5, 5}),
                arguments((Object) new byte[]{30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63, 5}),
                arguments((Object) new byte[]{30, -8, 114, 18, -37, -40, 99, -43, -113, -105, 49, -119, 121, -87, -63, 5})
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
        assertThatThrownBy(() -> identifierService.toInstant(identifier))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void extractNullInstant() {
        assertThat(identifierService.toInstant(null))
                .isNull();
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

    @Test
    void testInstantBounds() {
        final var now = Instant.now();
        assertThatThrownBy(() -> identifierService.asLowerBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> identifierService.asUpperBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testLocalDateBounds() {
        final var now = LocalDate.now();
        assertThatThrownBy(() -> identifierService.asLowerBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> identifierService.asUpperBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testLocalDateTimeBounds() {
        final var now = LocalDateTime.now();
        assertThatThrownBy(() -> identifierService.asLowerBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> identifierService.asUpperBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testOffsetDateTimeBounds() {
        final var now = OffsetDateTime.now();
        assertThatThrownBy(() -> identifierService.asLowerBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> identifierService.asUpperBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testZonedDateTimeBounds() {
        final var now = ZonedDateTime.now();
        assertThatThrownBy(() -> identifierService.asLowerBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> identifierService.asUpperBound(now))
                .isInstanceOf(UnsupportedOperationException.class);
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
    void convertListOfIdentifiersToText(final List<Identifier> inputs,
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
    void convertListOfIdentifiersToBinary(final List<Identifier> inputs,
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
    void convertSetOfIdentifiersToText(final Set<Identifier> inputs,
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
    void convertSetOfIdentifiersToBinary(final Set<Identifier> inputs,
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
