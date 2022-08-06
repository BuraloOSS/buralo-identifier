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
package com.buralo.identifier.uuid;

import com.buralo.identifier.api.IdentifierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestUUIDIdentifierService {

    private final IdentifierService identifierService = new UUIDIdentifierService();

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
                arguments("3TnesPWMmyqTHniq6DPq0F", new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5}),
                arguments("3TnesPWMmyuTHniq6DPq0F", new byte[]{17, -20, -22, -31, -88, 87, -53, -18, -98, 75, 59, -74, 28, -26, -74, 5})
        );
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodTextRepresentations(final String text, final byte[] binary) {
        assertThat(identifierService.fromText(text)).isEqualTo(new UUIDIdentifier(text, binary));
    }

    @ParameterizedTest
    @MethodSource("goodIdentifiers")
    void parseGoodBinaryRepresentations(final String text, final byte[] binary) {
        assertThat(identifierService.fromText(text)).isEqualTo(new UUIDIdentifier(text, binary));
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
}
