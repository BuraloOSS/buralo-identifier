/*
 *  Copyright 2026 Búraló Technologies
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
package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Unit test the parameter conversion.
 */
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
        return Stream.of(
                arguments(UUIDIdentifierService.V4, GOOD_ID1_STR, GOOD_ID1),
                arguments(UUIDIdentifierService.V4, GOOD_ID2_STR, GOOD_ID2),
                arguments(UUIDIdentifierService.V6, GOOD_ID3_STR, GOOD_ID3),
                arguments(UUIDIdentifierService.V6, GOOD_ID4_STR, GOOD_ID4),
                arguments(UUIDIdentifierService.V7, GOOD_ID5_STR, GOOD_ID5),
                arguments(UUIDIdentifierService.V7, GOOD_ID6_STR, GOOD_ID6)
        );
    }

    @ParameterizedTest
    @MethodSource
    void conversionSuccesses(final String uuidVersion,
                             final String text,
                             final Identifier identifier) {
        final var identifierConverter = new IdentifierParamConverter(UUIDIdentifierService.forVersion(uuidVersion));
        assertThat(identifierConverter.fromString(text)).isEqualTo(identifier);
    }

    static Stream<Arguments> conversionFailures() {
        return Stream.of(
                arguments(UUIDIdentifierService.V4, null),
                arguments(UUIDIdentifierService.V4, ""),
                arguments(UUIDIdentifierService.V4, " "),
                arguments(UUIDIdentifierService.V4, "zf3Wy94UIuel7UXWMryeI"),
                arguments(UUIDIdentifierService.V4, "zf3Wy94UIuel7UXWMryeIFF"),
                arguments(UUIDIdentifierService.V4, ":f3Wy94UIuel7UXWMryeIF"),
                arguments(UUIDIdentifierService.V4, GOOD_ID3_STR),
                arguments(UUIDIdentifierService.V4, GOOD_ID4_STR),
                arguments(UUIDIdentifierService.V4, GOOD_ID5_STR),
                arguments(UUIDIdentifierService.V4, GOOD_ID6_STR),

                arguments(UUIDIdentifierService.V6, null),
                arguments(UUIDIdentifierService.V6, ""),
                arguments(UUIDIdentifierService.V6, " "),
                arguments(UUIDIdentifierService.V6, "6jWm2dtNNDHE_n58TPb00"),
                arguments(UUIDIdentifierService.V6, "6jWm2dtNNDHE_n58TPb00FF"),
                arguments(UUIDIdentifierService.V6, ":jWm2dtNNDHE_n58TPb00F"),
                arguments(UUIDIdentifierService.V6, GOOD_ID1_STR),
                arguments(UUIDIdentifierService.V6, GOOD_ID2_STR),
                arguments(UUIDIdentifierService.V6, GOOD_ID5_STR),
                arguments(UUIDIdentifierService.V6, GOOD_ID6_STR),

                arguments(UUIDIdentifierService.V7, null),
                arguments(UUIDIdentifierService.V7, ""),
                arguments(UUIDIdentifierService.V7, " "),
                arguments(UUIDIdentifierService.V7, "-Tk3zAmZShTpkXSCMLOF2"),
                arguments(UUIDIdentifierService.V7, "-Tk3zAmZShTpkXSCMLOF2kk"),
                arguments(UUIDIdentifierService.V7, ":Tk3zAmZShTpkXSCMLOF2k"),
                arguments(UUIDIdentifierService.V7, GOOD_ID2_STR),
                arguments(UUIDIdentifierService.V7, GOOD_ID3_STR),
                arguments(UUIDIdentifierService.V7, GOOD_ID4_STR)
        );
    }

    @ParameterizedTest
    @MethodSource
    void conversionFailures(final String uuidVersion,
                            final String text) {
        final var identifierConverter = new IdentifierParamConverter(UUIDIdentifierService.forVersion(uuidVersion));
        assertThatThrownBy(() -> identifierConverter.fromString(text))
                .isInstanceOf(BadRequestException.class);
    }
}
