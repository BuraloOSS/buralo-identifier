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
package com.buralotech.oss.identifier.spring;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.uuid.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IdentifierConverterTest {

    private static final String GOOD_ID1_STR = "zf3Wy94UIuel7UXWMryeIF";

    private static final String GOOD_ID2_STR = "l0bK5MCbHUmOpG_nOkbw4k";

    private static final String GOOD_ID3_STR = "6jWm2dtNNDHE_n58TPb00F";

    private static final String GOOD_ID4_STR = "6jWm3hjNNxLE_n58TPb00F";

    private static final String GOOD_ID5_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    private static final String GOOD_ID6_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    private static final byte[] GOOD_ID1_BIN = {-2, -79, 33, -8, -95, 95, 79, -86, -79, 33, -8, -95, 95, 127, -86, 77};

    private static final byte[] GOOD_ID2_BIN = {-60, 25, -43, 25, 115, 103, 73, -4, -103, -43, 25, 115, 103, 9, -4, 23};

    private static final byte[] GOOD_ID3_BIN = {30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63, 5};

    private static final byte[] GOOD_ID4_BIN = {30, -8, 114, 18, -37, -40, 99, -43, -113, -105, 49, -119, 121, -87, -63, 5};

    private static final byte[] GOOD_ID5_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    private static final byte[] GOOD_ID6_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private static final Identifier GOOD_ID3 = new UUIDIdentifier(GOOD_ID3_STR, GOOD_ID3_BIN);

    private static final Identifier GOOD_ID4 = new UUIDIdentifier(GOOD_ID4_STR, GOOD_ID4_BIN);

    private static final Identifier GOOD_ID5 = new UUIDIdentifier(GOOD_ID5_STR, GOOD_ID5_BIN);

    private static final Identifier GOOD_ID6 = new UUIDIdentifier(GOOD_ID6_STR, GOOD_ID6_BIN);

    static Stream<Arguments> conversionSuccesses() {
        final var version4Delegate = new UUIDVersion4Delegate();
        final var version6Delegate = new UUIDVersion6Delegate();
        final var version7Delegate = new UUIDVersion7Delegate();
        return Stream.of(
                arguments(version4Delegate, GOOD_ID1_STR, GOOD_ID1),
                arguments(version4Delegate, GOOD_ID2_STR, GOOD_ID2),
                arguments(version6Delegate, GOOD_ID3_STR, GOOD_ID3),
                arguments(version6Delegate, GOOD_ID4_STR, GOOD_ID4),
                arguments(version7Delegate, GOOD_ID5_STR, GOOD_ID5),
                arguments(version7Delegate, GOOD_ID6_STR, GOOD_ID6)
        );
    }

    @ParameterizedTest
    @MethodSource
    void conversionSuccesses(final UUIDVersionDelegate delegate,
                             final String text,
                             final Identifier identifier) {
        final var identifierConverter = new IdentifierConverter(new UUIDIdentifierService(delegate));
        assertThat(identifierConverter.convert(text)).isEqualTo(identifier);
    }

    static Stream<Arguments> conversionFailures() {
        final var version4Delegate = new UUIDVersion4Delegate();
        final var version6Delegate = new UUIDVersion6Delegate();
        final var version7Delegate = new UUIDVersion7Delegate();
        return Stream.of(
                arguments(version4Delegate, null),
                arguments(version4Delegate, ""),
                arguments(version4Delegate, " "),
                arguments(version4Delegate, "zf3Wy94UIuel7UXWMryeI"),
                arguments(version4Delegate, "zf3Wy94UIuel7UXWMryeIFF"),
                arguments(version4Delegate, ":f3Wy94UIuel7UXWMryeIF"),
                arguments(version4Delegate, GOOD_ID3_STR),
                arguments(version4Delegate, GOOD_ID4_STR),
                arguments(version4Delegate, GOOD_ID5_STR),
                arguments(version4Delegate, GOOD_ID6_STR),

                arguments(version6Delegate, null),
                arguments(version6Delegate, ""),
                arguments(version6Delegate, " "),
                arguments(version6Delegate, "6jWm2dtNNDHE_n58TPb00"),
                arguments(version6Delegate, "6jWm2dtNNDHE_n58TPb00FF"),
                arguments(version6Delegate, ":jWm2dtNNDHE_n58TPb00F"),
                arguments(version6Delegate, GOOD_ID1_STR),
                arguments(version6Delegate, GOOD_ID2_STR),
                arguments(version6Delegate, GOOD_ID5_STR),
                arguments(version6Delegate, GOOD_ID6_STR),

                arguments(version7Delegate, null),
                arguments(version7Delegate, ""),
                arguments(version7Delegate, " "),
                arguments(version7Delegate, "-Tk3zAmZShTpkXSCMLOF2"),
                arguments(version7Delegate, "-Tk3zAmZShTpkXSCMLOF2kk"),
                arguments(version7Delegate, ":Tk3zAmZShTpkXSCMLOF2k"),
                arguments(version7Delegate, GOOD_ID2_STR),
                arguments(version7Delegate, GOOD_ID3_STR),
                arguments(version7Delegate, GOOD_ID4_STR)
        );
    }


    @ParameterizedTest
    @MethodSource
    void conversionFailures(final UUIDVersionDelegate delegate,
                            final String text) {
        final var identifierConverter = new IdentifierConverter(new UUIDIdentifierService(delegate));
        assertThatThrownBy(() -> identifierConverter.convert(text))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
