/*
 *  Copyright 2025-2026 Búraló Technologies
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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.r2dbc.postgresql.client.EncodedParameter;
import io.r2dbc.postgresql.codec.Codec;
import io.r2dbc.postgresql.codec.PostgresqlObjectId;
import io.r2dbc.postgresql.message.Format;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static io.r2dbc.postgresql.message.Format.FORMAT_BINARY;

public class IdentifierCodec implements Codec<Identifier> {

    private static final int OID = PostgresqlObjectId.BYTEA.getObjectId();

    private final IdentifierService identifierService;

    public IdentifierCodec(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public boolean canDecode(final int dataType,
                             @NonNull final Format format,
                             @NonNull final Class<?> type) {
        return dataType == OID && Identifier.class.isAssignableFrom(type);
    }

    @Override
    public boolean canEncode(@NonNull final Object value) {
        return Identifier.class.isAssignableFrom(value.getClass());
    }

    @Override
    public boolean canEncodeNull(@NonNull final Class<?> type) {
        return Identifier.class.isAssignableFrom(type);
    }

    @Override
    public @Nullable Identifier decode(@Nullable final ByteBuf buffer,
                                       final int dataType,
                                       @NonNull final Format format,
                                       @NonNull final Class<? extends Identifier> type) {
        if (dataType != OID) {
            throw new IllegalArgumentException("Cannot decode value. OID %d is not supported by this codec.".formatted(dataType));
        }
        if (!Identifier.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Cannot decode value. Class %s is not supported by this codec.".formatted(type.getSimpleName()));
        }
        if (buffer == null) {
            return null;
        }
        return switch (format) {
            case FORMAT_BINARY -> decodeBinary(buffer);
            case FORMAT_TEXT -> decodeText(buffer);
        };
    }

    private @Nullable Identifier decodeBinary(@NonNull final ByteBuf buffer) {
        return switch (buffer.readableBytes()) {
            case 0 -> null;
            case 16 -> identifierService.fromByteBuffer(buffer);
            default -> throw new IllegalArgumentException("Cannot decode Identifier");
        };
    }

    private @Nullable Identifier decodeText(@NonNull final ByteBuf buffer) {
        return switch (buffer.readableBytes()) {
            case 0 -> null;
            case 34 -> {
                final var text = buffer.readString(34, StandardCharsets.ISO_8859_1);
                yield identifierService.fromBinary(text.substring(2));
            }
            default -> throw new IllegalArgumentException("Cannot decode Identifier");
        };
    }

    @Override
    @NonNull
    public EncodedParameter encode(@NonNull final Object value) {
        return new EncodedParameter(FORMAT_BINARY, OID, Mono.defer(() -> Mono.just(Unpooled.wrappedBuffer(((Identifier) value).binary()))));
    }

    @Override
    @NonNull
    public EncodedParameter encode(@NonNull final Object value,
                                   final int dataType) {
        if (dataType == OID) {
            return encode(value);
        } else {
            throw new IllegalArgumentException("Cannot encode value. OID %d is not supported by this codec.".formatted(dataType));
        }
    }

    @Override
    @NonNull
    public EncodedParameter encodeNull() {
        return new EncodedParameter(FORMAT_BINARY, OID, EncodedParameter.NULL_VALUE);
    }
}
