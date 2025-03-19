package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static com.buralotech.oss.identifier.uuid.TestData.*;

class TestUUIDIdentifier {


    static Stream<Arguments> verifyConversions() {
        return Stream.of(
                arguments(GOOD_ID1, GOOD_ID1_STR, GOOD_ID1_HEX, GOOD_ID1_BIN, GOOD_ID1_UUID_STR),
                arguments(GOOD_ID2, GOOD_ID2_STR, GOOD_ID2_HEX, GOOD_ID2_BIN, GOOD_ID2_UUID_STR),
                arguments(GOOD_ID3, GOOD_ID3_STR, GOOD_ID3_HEX, GOOD_ID3_BIN, GOOD_ID3_UUID_STR),
                arguments(GOOD_ID4, GOOD_ID4_STR, GOOD_ID4_HEX, GOOD_ID4_BIN, GOOD_ID4_UUID_STR),
                arguments(GOOD_ID5, GOOD_ID5_STR, GOOD_ID5_HEX, GOOD_ID5_BIN, GOOD_ID5_UUID_STR),
                arguments(GOOD_ID6, GOOD_ID6_STR, GOOD_ID6_HEX, GOOD_ID6_BIN, GOOD_ID6_UUID_STR));
    }

    @ParameterizedTest
    @MethodSource
    void verifyConversions(final Identifier id,
                           final String text,
                           final String hexString,
                           final byte[] binary,
                           final String uuidString) {
        assertThat(id.text()).isEqualTo(text);
        assertThat(id.hex()).isEqualTo(hexString);
        assertThat(id.binary()).isEqualTo(binary);
        assertThat(id.uuidString()).isEqualTo(uuidString);
        assertThat(id.uuid()).isEqualTo(UUID.fromString(uuidString));
    }

    private static Stream<Arguments> canCompareTwoSameObjects() {
        return Stream.of(
                arguments(GOOD_ID1, GOOD_ID1),
                arguments(GOOD_ID2, GOOD_ID2),
                arguments(GOOD_ID3, GOOD_ID3),
                arguments(GOOD_ID4, GOOD_ID4),
                arguments(GOOD_ID5, GOOD_ID5),
                arguments(GOOD_ID6, GOOD_ID6));
    }

    @ParameterizedTest
    @MethodSource
    void canCompareTwoSameObjects(final Identifier lhs,
                                  final Identifier rhs) {
        assertThat(lhs.equals(rhs)).isTrue();
        assertThat(rhs.equals(lhs)).isTrue();
    }

    private static Stream<Arguments> canCompareTwoEquivalentObjects() {
        return Stream.of(
                arguments(GOOD_ID1, GOOD_ID1_STR, GOOD_ID1_HEX, GOOD_ID1_BIN, GOOD_ID1_UUID_STR),
                arguments(GOOD_ID2, GOOD_ID2_STR, GOOD_ID2_HEX, GOOD_ID2_BIN, GOOD_ID2_UUID_STR),
                arguments(GOOD_ID3, GOOD_ID3_STR, GOOD_ID3_HEX, GOOD_ID3_BIN, GOOD_ID3_UUID_STR),
                arguments(GOOD_ID4, GOOD_ID4_STR, GOOD_ID4_HEX, GOOD_ID4_BIN, GOOD_ID4_UUID_STR),
                arguments(GOOD_ID5, GOOD_ID5_STR, GOOD_ID5_HEX, GOOD_ID5_BIN, GOOD_ID5_UUID_STR),
                arguments(GOOD_ID6, GOOD_ID6_STR, GOOD_ID6_HEX, GOOD_ID6_BIN, GOOD_ID6_UUID_STR));
    }

    @ParameterizedTest
    @MethodSource
    void canCompareTwoEquivalentObjects(final Identifier id,
                                        final String text,
                                        final String hex,
                                        final byte[] binary,
                                        final String uuidString) {
        assertThat(new UUIDIdentifier(text, binary)).isEqualTo(id);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(new UUIDIdentifier(text, hex)).isEqualTo(id);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, hex));
        assertThat(id).isEqualTo(text);
        assertThat(id).isEqualTo(hex);
        assertThat(id).isEqualTo(uuidString);
        assertThat(id).isEqualTo(UUID.fromString(uuidString));
        assertThat(id).isEqualTo(binary);
    }

    private static Stream<Arguments> cannotCompareDifferentObjects() {
        return Stream.of(
                arguments(GOOD_ID1, null),
                arguments(GOOD_ID1, new Object()),
                arguments(GOOD_ID1, GOOD_ID2),
                arguments(GOOD_ID1, GOOD_ID3),
                arguments(GOOD_ID1, GOOD_ID4),
                arguments(GOOD_ID1, GOOD_ID5),
                arguments(GOOD_ID1, GOOD_ID6));
    }

    @ParameterizedTest
    @MethodSource
    void cannotCompareDifferentObjects(final Identifier lhs,
                                       final Object rhs) {
        assertThat(lhs.equals(rhs)).isFalse();
    }
}
