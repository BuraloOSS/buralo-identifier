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

public class IdentifierArrayCodec implements Codec<Identifier[]> {

    private static final int OID = PostgresqlObjectId.BYTEA_ARRAY.getObjectId();

    private final IdentifierService identifierService;

    public IdentifierArrayCodec(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public boolean canDecode(final int dataType,
                             @NonNull final Format format,
                             @NonNull final Class<?> type) {
        return dataType == OID && Identifier[].class.isAssignableFrom(type);
    }

    @Override
    public boolean canEncode(@NonNull final Object value) {
        return Identifier[].class.isAssignableFrom(value.getClass());
    }

    @Override
    public boolean canEncodeNull(@NonNull final Class<?> type) {
        return Identifier[].class.isAssignableFrom(type);
    }

    @Override
    public @Nullable Identifier[] decode(@Nullable final ByteBuf buffer,
                                         final int dataType,
                                         @NonNull final Format format,
                                         @NonNull final Class<? extends Identifier[]> type) {
        if (dataType != OID) {
            throw new IllegalArgumentException("Cannot decode value. OID %d is not supported by this codec.".formatted(dataType));
        }
        if (!Identifier[].class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Cannot decode value. Class %s is not supported by this codec.".formatted(type.getSimpleName()));
        }
        if (buffer == null) {
            return null;
        }
        return switch (format) {
            case FORMAT_BINARY -> {
                final var results = new Identifier[buffer.readableBytes() / 16];
                int i = 0;
                while (buffer.readableBytes() >= 16) {
                    results[i++] = identifierService.fromByteBuffer(buffer);
                }
                if (buffer.readableBytes() != 0) {
                    throw new IllegalArgumentException("Cannot decode Identifier[]");
                }
                yield results;
            }
            case FORMAT_TEXT -> {
                if(buffer.readableBytes() < 2) {
                    throw new IllegalArgumentException("Cannot decode Identifier[]");
                }
                if(buffer.readableBytes() == 2) {
                    final var first = buffer.readByte();
                    final var second = buffer.readByte();
                    if (first == '{' && second == '}') {
                        yield new Identifier[0];
                    } else {
                        throw new IllegalArgumentException("Cannot decode Identifier[]");
                    }
                }
                final var results = new Identifier[buffer.readableBytes() / 38];
                int i = 0;
                while (buffer.readableBytes() >= 38) {
                    final var text = buffer.readString(38, StandardCharsets.ISO_8859_1);
                    if (text.startsWith(i == 0 ? "{\"\\\\x" : ",\"\\\\x") && text.endsWith("\"")) {
                        results[i++] = identifierService.fromBinary(text.substring(5, 37));
                    } else {
                        throw new IllegalArgumentException("Cannot decode Identifier[]");
                    }
                }
                if (buffer.readableBytes() != 1 && buffer.readByte() != '}') {
                    throw new IllegalArgumentException("Cannot decode Identifier[]");
                }
                yield results;
            }
        };
    }

    @Override
    @NonNull
    public EncodedParameter encode(@NonNull final Object value) {
        return new EncodedParameter(FORMAT_BINARY, OID, Mono.defer(() -> {
            final var ids = (Identifier[]) value;
            final var buffer = Unpooled.buffer(16 * ids.length);
            for (var id : ids) {
                id.writeTo(buffer);
            }
            return Mono.just(buffer);
        }));
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
