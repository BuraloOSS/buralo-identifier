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
package com.buralotech.oss.identifier.spring.r2dbc.postgresql;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import io.netty.buffer.Unpooled;
import io.r2dbc.postgresql.message.Format;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestIdentifierArrayCodec {

    private static final IdentifierService identifierService = UUIDIdentifierService.forVersion(UUIDIdentifierService.V7);

    private final IdentifierArrayCodec codec = new IdentifierArrayCodec(identifierService);

    @Test
    void canEncodeNullIdentifier() {
        assertThat(codec.canEncodeNull(Identifier[].class))
                .isTrue();
    }

    @Test
    void cannotEncodeNullNonIdentifier() {
        assertThat(codec.canEncodeNull(Object[].class))
                .isFalse();
    }

    @Test
    void canEncodeIdentifier() {
        final var id = identifierService.generate();
        assertThat(codec.canEncode(new Identifier[]{id}))
                .isTrue();
    }

    @Test
    void cannotEncodeNonIdentifier() {
        final var id = identifierService.generate();
        assertThat(codec.canEncode(new String[]{id.hex()}))
                .isFalse();
    }

    @Test
    void encodeNull() {
        assertThat(codec.encodeNull())
                .hasToString("Parameter{format=FORMAT_BINARY, type=1001, value=FluxEmpty}");
    }

    @Test
    void encodeNonNull() {
        final var id = identifierService.generate();
        assertThat(codec.encode(new Identifier[]{id}).toString())
                .matches(Pattern.compile("^Parameter\\{format=FORMAT_BINARY, type=1001, value=.*}$"));
    }

    @Test
    void encodeNonNullWithDataType() {
        final var id = identifierService.generate();
        assertThat(codec.encode(new Identifier[]{id}, 1001).toString())
                .matches(Pattern.compile("^Parameter\\{format=FORMAT_BINARY, type=1001, value=.*}$"));
    }

    @Test
    void encodeNonNullWithInvalidDataType() {
        final var id = identifierService.generate();
        assertThatThrownBy(() -> codec.encode(new Identifier[]{id}, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot encode value. OID 1 is not supported by this codec.");
    }

    @Test
    void canDecodeIdentifierFromBinary() {
        assertThat(codec.canDecode(1001, Format.FORMAT_BINARY, Identifier[].class))
                .isTrue();
    }

    @Test
    void canDecodeIdentifierFromText() {
        assertThat(codec.canDecode(1001, Format.FORMAT_TEXT, Identifier[].class))
                .isTrue();
    }

    @Test
    void canDecodeIdentifierWithWrongOID() {
        assertThat(codec.canDecode(1, Format.FORMAT_BINARY, Identifier[].class))
                .isFalse();
    }

    @Test
    void canDecodeIdentifierWithUnsupportedType() {
        assertThat(codec.canDecode(1001, Format.FORMAT_BINARY, String.class))
                .isFalse();
    }

    @Test
    void decodeNull() {
        assertThat(codec.decode(null, 1001, Format.FORMAT_BINARY, Identifier[].class))
                .isNull();
    }

    @Test
    void decodeEmptyBinary() {
        assertThat(codec.decode(Unpooled.EMPTY_BUFFER, 1001, Format.FORMAT_BINARY, Identifier[].class))
                .isEmpty();
    }

    @Test
    void decodeEmptyText() {
        assertThat(codec.decode(Unpooled.copiedBuffer("{}", StandardCharsets.ISO_8859_1), 1001, Format.FORMAT_TEXT, Identifier[].class))
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = 3)
    void decodeText(final int count) {
        final var ids = identifierService.generateList(count);
        final var text = "{" + ids.stream().map(Identifier::hex).map(hex -> "\"\\\\x" + hex + "\"").collect(Collectors.joining(",")) + "}";
        final var buffer = Unpooled.copiedBuffer(text, StandardCharsets.ISO_8859_1);
        assertThat(codec.decode(buffer, 1001, Format.FORMAT_TEXT, Identifier[].class))
                .containsExactlyElementsOf(ids);
    }

    @ParameterizedTest
    @ValueSource(ints = 3)
    void decodeBinary(final int count) {
        final var ids = identifierService.generateList(count);
        final var binary = new byte[count * 16];
        for (int i = 0; i < count; i++) {
            System.arraycopy(ids.get(i).binary(), 0, binary, i * 16, 16);
        }
        final var buffer = Unpooled.copiedBuffer(binary);
        assertThat(codec.decode(buffer, 1001, Format.FORMAT_BINARY, Identifier[].class))
                .containsExactlyElementsOf(ids);
    }

    static Stream<Arguments> cannotDecodeGarbageText() {
        return Stream.of(
                arguments(""),
                arguments("{"),
                arguments("}"),
                arguments("}}"),
                arguments("{{"),
                arguments("\\x0000"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b!\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b77\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b7\"]"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b7\",\"\\x01a12bc0a1f57f3cbdcc56ee399c172!\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b7\",\"\\x01a12bc0a1f57f3cbdcc56ee399c172\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b7\",\"\\x01a12bc0a1f57f3cbdcc56ee399c17244\"}"),
                arguments("{\"\\x01a12bc0a1f57d32ad9b2b2053ab33b7\",\"\\x01a12bc0a1f57f3cbdcc56ee399c1724\"]")
        );
    }

    @ParameterizedTest
    @MethodSource
    void cannotDecodeGarbageText(final String text) {
        final var buffer = Unpooled.copiedBuffer(text, StandardCharsets.ISO_8859_1);
        assertThatThrownBy(() -> codec.decode(buffer, 1001, Format.FORMAT_TEXT, Identifier[].class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode Identifier[]");
    }

    static Stream<Arguments> cannotDecodeGarbageBinary() {
        return Stream.of(
                arguments((Object) new byte[]{0, 0, 0, 0}),
                arguments((Object) HexFormat.of().parseHex("01a12bc0a1f57d32ad9b2b2053ab33b7000000"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void cannotDecodeGarbageBinary(final byte[] binary) {
        final var buffer = Unpooled.copiedBuffer(binary);
        assertThatThrownBy(() -> codec.decode(buffer, 1001, Format.FORMAT_BINARY, Identifier[].class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode Identifier[]");
    }

    @Test
    void cannotDecodeBinaryIfOIDIsWrong() {
        final var id = identifierService.generate();
        final var buffer = Unpooled.copiedBuffer(id.binary());
        assertThatThrownBy(() -> codec.decode(buffer, 1, Format.FORMAT_BINARY, Identifier[].class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode value. OID 1 is not supported by this codec.");
    }
}