package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestUUIDIdentifier {

    private static final String GOOD_ID1_STR = "zf3Wy94UIuel7UXWMryeIF";

    private static final String GOOD_ID2_STR = "l0bK5MCbHUmOpG_nOkbw4k";

    private static final String GOOD_ID1_HEX = "feb121f8a15f4faab121f8a15f7faa4d";

    private static final String GOOD_ID2_HEX = "c419d519736749fc99d519736709fc17";

    private static final String GOOD_ID1_UUID_STR = "feb121f8-a15f-4faa-b121-f8a15f7faa4d";

    private static final String GOOD_ID2_UUID_STR = "c419d519-7367-49fc-99d5-19736709fc17";

    private static final byte[] GOOD_ID1_BIN = {-2, -79, 33, -8, -95, 95, 79, -86, -79, 33, -8, -95, 95, 127, -86, 77};

    private static final byte[] GOOD_ID2_BIN = {-60, 25, -43, 25, 115, 103, 73, -4, -103, -43, 25, 115, 103, 9, -4, 23};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private static final String GOOD_ID3_STR = "6jWm2dtNNDHE_n58TPb00F";

    private static final String GOOD_ID4_STR = "6jWm3hjNNxLE_n58TPb00F";

    private static final String GOOD_ID3_HEX = "1ef8720e9e5860e48f97318979a9c105";

    private static final String GOOD_ID4_HEX = "1ef87212dbd863d58f97318979a9c105";

    private static final String GOOD_ID3_UUID_STR = "1ef8720e-9e58-60e4-8f97-318979a9c105";

    private static final String GOOD_ID4_UUID_STR = "1ef87212-dbd8-63d5-8f97-318979a9c105";

    private static final byte[] GOOD_ID3_BIN = {30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63, 5};

    private static final byte[] GOOD_ID4_BIN = {30, -8, 114, 18, -37, -40, 99, -43, -113, -105, 49, -119, 121, -87, -63, 5};

    private static final Identifier GOOD_ID3 = new UUIDIdentifier(GOOD_ID3_STR, GOOD_ID3_BIN);

    private static final Identifier GOOD_ID4 = new UUIDIdentifier(GOOD_ID4_STR, GOOD_ID4_BIN);

    private static final String GOOD_ID5_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    private static final String GOOD_ID6_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    private static final String GOOD_ID5_HEX = "01ec04fcbca476d7b5c2274d5d66500f";

    private static final String GOOD_ID6_HEX = "01ec04fcbca47de5a1b518482cc4b361";

    private static final String GOOD_ID5_UUID_STR = "01ec04fc-bca4-76d7-b5c2-274d5d66500f";

    private static final String GOOD_ID6_UUID_STR = "01ec04fc-bca4-7de5-a1b5-18482cc4b361";

    private static final byte[] GOOD_ID5_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    private static final byte[] GOOD_ID6_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    private static final Identifier GOOD_ID5 = new UUIDIdentifier(GOOD_ID5_STR, GOOD_ID5_BIN);

    private static final Identifier GOOD_ID6 = new UUIDIdentifier(GOOD_ID6_STR, GOOD_ID6_BIN);

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
                arguments(GOOD_ID1, GOOD_ID1_STR, GOOD_ID1_HEX, GOOD_ID1_BIN),
                arguments(GOOD_ID2, GOOD_ID2_STR, GOOD_ID2_HEX, GOOD_ID2_BIN),
                arguments(GOOD_ID3, GOOD_ID3_STR, GOOD_ID3_HEX, GOOD_ID3_BIN),
                arguments(GOOD_ID4, GOOD_ID4_STR, GOOD_ID4_HEX, GOOD_ID4_BIN),
                arguments(GOOD_ID5, GOOD_ID5_STR, GOOD_ID5_HEX, GOOD_ID5_BIN),
                arguments(GOOD_ID6, GOOD_ID6_STR, GOOD_ID6_HEX, GOOD_ID6_BIN));
    }

    @ParameterizedTest
    @MethodSource
    void canCompareTwoEquivalentObjects(final Identifier id,
                                        final String text,
                                        final String hex,
                                        final byte[] binary) {
        assertThat(new UUIDIdentifier(text, binary)).isEqualTo(id);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, binary));
        assertThat(new UUIDIdentifier(text, hex)).isEqualTo(id);
        assertThat(id).isEqualTo(new UUIDIdentifier(text, hex));
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
