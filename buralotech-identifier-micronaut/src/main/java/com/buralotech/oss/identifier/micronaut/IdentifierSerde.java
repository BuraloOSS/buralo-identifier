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
package com.buralotech.oss.identifier.micronaut;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serde;

import java.io.IOException;

/**
 * Serialization of identifier fields.
 */
public class IdentifierSerde implements Serde<Identifier> {

    /**
     * The identifier service is required for deserialization.
     */
    private final IdentifierService identifierService;

    /**
     * Initialize the serializer/deserializer injecting the identifier service.
     *
     * @param identifierService Required for deserialization.
     */
    public IdentifierSerde(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    /**
     * Serialize the identifier value as a string.
     *
     * @param encoder The encoder to use
     * @param context The encoder context, never {@code null}
     * @param type    Models the generic type of the value
     * @param value   The value to serialize, never {@code null}. Callers should encode null values directly.
     * @throws IOException If there was an error writing the string.
     */
    @Override
    public void serialize(final Encoder encoder,
                          final EncoderContext context,
                          final Argument<? extends Identifier> type,
                          final Identifier value)
            throws IOException {
        encoder.encodeString(value.text());
    }

    /**
     * Deserialize an identifier value from a string.
     *
     * @param decoder The decoder, never {@code null}
     * @param context The decoder context, never {@code null}
     * @param type    The generic type to be deserialized
     * @return The deserialized identifier.
     * @throws IOException If there was an error reading the string.
     */
    @Override
    public Identifier deserialize(final Decoder decoder,
                                  final DecoderContext context,
                                  final Argument<? super Identifier> type)
            throws IOException {
        final var value = decoder.decodeString();
        return identifierService.fromText(value);
    }
}
