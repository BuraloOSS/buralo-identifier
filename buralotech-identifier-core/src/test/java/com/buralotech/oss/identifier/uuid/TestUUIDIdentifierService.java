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
package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.IdentifierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
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

    @Test
    void extractInstant() {
        final var identifier = identifierService.generate();
         assertThat(identifierService.toInstant(identifier)).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
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
}
