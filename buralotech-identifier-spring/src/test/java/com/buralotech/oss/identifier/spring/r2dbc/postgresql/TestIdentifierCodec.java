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

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestIdentifierCodec {

    private final IdentifierService identifierService = UUIDIdentifierService.forVersion(UUIDIdentifierService.V7);

    private final IdentifierCodec codec = new IdentifierCodec(identifierService);

    @Test
    void canEncodeNullIdentifier() {
        assertThat(codec.canEncodeNull(Identifier.class))
                .isTrue();
    }

    @Test
    void cannotEncodeNullNonIdentifier() {
        assertThat(codec.canEncodeNull(Object.class))
                .isFalse();
    }

    @Test
    void canEncodeIdentifier() {
        final var id = identifierService.generate();
        assertThat(codec.canEncode(id))
                .isTrue();
    }

    @Test
    void cannotEncodeNonIdentifier() {
        final var id = identifierService.generate();
        assertThat(codec.canEncode(id.hex()))
                .isFalse();
    }

    @Test
    void encodeNull() {
        assertThat(codec.encodeNull())
                .hasToString("Parameter{format=FORMAT_BINARY, type=17, value=FluxEmpty}");
    }

    @Test
    void encodeNonNull() {
        final var id = identifierService.generate();
        assertThat(codec.encode(id).toString())
                .matches(Pattern.compile("^Parameter\\{format=FORMAT_BINARY, type=17, value=.*}$"));
    }

    @Test
    void encodeNonNullWithDataType() {
        final var id = identifierService.generate();
        assertThat(codec.encode(id, 17).toString())
                .matches(Pattern.compile("^Parameter\\{format=FORMAT_BINARY, type=17, value=.*}$"));
    }

    @Test
    void encodeNonNullWithInvalidDataType() {
        final var id = identifierService.generate();
        assertThatThrownBy(() -> codec.encode(id, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot encode value. OID 1 is not supported by this codec.");
    }

    @Test
    void canDecodeIdentifierFromBinary() {
        assertThat(codec.canDecode(17, Format.FORMAT_BINARY, Identifier.class))
                .isTrue();
    }

    @Test
    void canDecodeIdentifierFromText() {
        assertThat(codec.canDecode(17, Format.FORMAT_TEXT, Identifier.class))
                .isTrue();
    }

    @Test
    void cannotDecodeIdentifierWithWrongOID() {
        assertThat(codec.canDecode(1, Format.FORMAT_BINARY, Identifier.class))
                .isFalse();
    }

    @Test
    void cannotDecodeIdentifierWithUnsupportedType() {
        assertThat(codec.canDecode(17, Format.FORMAT_BINARY, String.class))
                .isFalse();
    }

    @Test
    void decodeNull() {
        assertThat(codec.decode(null, 17, Format.FORMAT_BINARY, Identifier.class))
                .isNull();
    }

    @Test
    void decodeEmptyBinary() {
        assertThat(codec.decode(Unpooled.EMPTY_BUFFER, 17, Format.FORMAT_BINARY, Identifier.class))
                .isNull();
    }

    @Test
    void decodeEmptyText() {
        assertThat(codec.decode(Unpooled.EMPTY_BUFFER, 17, Format.FORMAT_TEXT, Identifier.class))
                .isNull();
    }

    @Test
    void decodeText() {
        final var id = identifierService.generate();
        final var buffer = Unpooled.copiedBuffer("\\x" + id.hex(), StandardCharsets.ISO_8859_1);
        assertThat(codec.decode(buffer, 17, Format.FORMAT_TEXT, Identifier.class))
                .isEqualTo(id);
    }

    @Test
    void decodeBinary() {
        final var id = identifierService.generate();
        final var buffer = Unpooled.copiedBuffer( id.binary());
        assertThat(codec.decode(buffer, 17, Format.FORMAT_BINARY, Identifier.class))
                .isEqualTo(id);
    }

    @Test
    void cannotDecodeGarbageText() {
        final var buffer = Unpooled.copiedBuffer("\\x0000", StandardCharsets.ISO_8859_1);
        assertThatThrownBy(() -> codec.decode(buffer, 17, Format.FORMAT_TEXT, Identifier.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode Identifier");
    }

    @Test
    void cannotDecodeGarbageBinary() {
        final var buffer = Unpooled.copiedBuffer(new byte[]{0,0,0,0});
        assertThatThrownBy(() -> codec.decode(buffer, 17, Format.FORMAT_BINARY, Identifier.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode Identifier");
    }

    @Test
    void cannotDecodeBinaryIfOIDIsWrong() {
        final var id = identifierService.generate();
        final var buffer = Unpooled.copiedBuffer( id.binary());
        assertThatThrownBy(() -> codec.decode(buffer, 1, Format.FORMAT_BINARY, Identifier.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot decode value. OID 1 is not supported by this codec.");
    }
}
