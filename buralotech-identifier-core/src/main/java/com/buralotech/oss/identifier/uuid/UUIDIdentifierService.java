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
package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;

import java.nio.ByteBuffer;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Generate identifiers and parse binary and textual representations of identifiers. The generator uses either a Type 1
 * UUID generator and juggles the bits so that the binary representations can be ordered by generation time or a new
 * Type 6 UUID which is reordered in a similar way but is standardised. The textual representation is a modified
 * URL-safe base 64 encoding that is also sortable.
 */
public final class UUIDIdentifierService implements IdentifierService {

    /**
     * Pattern used to verify UUID strings.
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    /**
     * Look-up table used during encoding.
     */
    private static final char[] ENCODING = {
            '-', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'
    };

    /**
     * Look-up table used during decoding.
     */
    private static final int[] DECODING = new int[256];

    /*
     * Initialise the array used for look-ups during decoding.
     */
    static {
        // First mark all the values as invalid (-1)

        Arrays.fill(DECODING, -1);

        // Now map all the supported Base64 digits to their ordinal value

        for (int i = 0; i < ENCODING.length; i++) {
            DECODING[ENCODING[i]] = i;
        }
    }

    /**
     * Number of ticks per millisecond. The UUID tick is 100 nanoseconds.
     */
    private static final long TICKS_PER_MILLISECOND = 10000L;

    /**
     * Number of ticks per second. TheUUID thick is 100 nanoseconds.
     */
    private static final long TICKS_PER_SECOND = 10000000L;

    /**
     * The adjustment to apply to convert UUID epoch to Unix Epoch. The UUID epoch 15 October 1582, while the Unix
     * epoch is 1 January 1970.
     */
    private static final long EPOCH_ADJ = 122192928000000000L;

    /**
     * Delegate that encapsulates logic that is specific to the UUID format.
     */
    private final UUIDVersionDelegate delegate;

    /**
     * Constructor used to inject the delegate that encapsulates the logic that is specific to the UUID format.
     *
     * @param delegate Encapsulates logic that is specific to the UUID format.
     */
    public UUIDIdentifierService(final UUIDVersionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier.
     */
    @Override
    public Identifier generate() {
        final var binary = delegate.generate();
        final var text = encode(binary);
        return new UUIDIdentifier(text, binary);
    }

    /**
     * Decode an identifier using its text representation.
     *
     * @param text The text representation.
     * @return The identifier.
     */
    @Override
    public Identifier fromText(final String text) {
        if (text == null || !delegate.isValidText(text)) {
            throw new IllegalArgumentException("invalid text representation of identifier");
        }
        final var binary = decode(text);
        return new UUIDIdentifier(text, binary);
    }

    /**
     * Decode an identifier using its binary representation.
     *
     * @param binary The binary representation.
     * @return The identifier.
     */
    @Override
    public Identifier fromBinary(final byte[] binary) {
        if (binary == null || binary.length != 16 || !delegate.isValidBinary(binary, 0)) {
            throw new IllegalArgumentException("invalid binary representation of identifier");
        }
        return doFromBinary(binary);
    }

    /**
     * Decode an identifier using its binary representation starting at an offset.
     *
     * @param binary The binary representation.
     * @param offset The position in the byte array from which to read.
     * @return The identifier.
     */
    @Override
    public Identifier fromBinary(final byte[] binary,
                                 final int offset) {
        if (binary == null || binary.length < offset + 16 || !delegate.isValidBinary(binary, offset)) {
            throw new IllegalArgumentException("invalid binary representation of identifier");
        }
        return doFromBinary(Arrays.copyOfRange(binary, offset, offset + 16));
    }

    /**
     * Decode an identifier using its binary representation from a {@link ByteBuffer}.
     *
     * @param buffer Contains the binary representation of the identifier.
     * @return The identifier.
     */
    @Override
    public Identifier fromByteBuffer(final ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < 16) {
            throw new IllegalArgumentException("invalid binary representation of identifier");
        }
        final var binary = new byte[16];
        buffer.get(binary);
        if (!delegate.isValidBinary(binary, 0)) {
            throw new IllegalArgumentException("invalid binary representation of identifier");
        }
        return doFromBinary(binary);
    }

    /**
     * Construct calculate the text representation and return an identifier encapsulating
     * the binary and text representations.
     *
     * @param binary The binary representation.
     * @return The identifier.
     */
    private Identifier doFromBinary(final byte[] binary) {
        final var text = encode(binary);
        return new UUIDIdentifier(text, binary);
    }

    /**
     * Create an identifier from a UUID string.
     *
     * @param uuid The UUID string.
     * @return The identifier.
     */
    @Override
    public Identifier fromUUID(final String uuid) {
        if (uuid == null) {
            return null;
        }
        if (!UUID_PATTERN.matcher(uuid).matches()) {
            throw new IllegalArgumentException("invalid UUID representation of identifier");
        }
        return fromBinary(uuid.replace("-", ""));
    }

    /**
     * Create an identifier from a UUID.
     *
     * @param uuid The UUID.
     * @return The identifier.
     */
    @Override
    public Identifier fromUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fromBinary(uuid.toString().replace("-", ""));
    }

    /**
     * Encode 16 bytes as 22 Base64 digit string.
     *
     * @param bytes The input bytes.
     * @return The 22 digit Base64 string.
     */
    private String encode(final byte[] bytes) {
        assert bytes != null && bytes.length == 16;
        final var chars = new char[22];
        var readPos = 0;
        var writePos = 0;
        do {
            encode3(bytes[readPos], bytes[readPos + 1], bytes[readPos + 2], chars, writePos);
            readPos += 3;
            writePos += 4;
        } while (readPos < 15);
        encode1(bytes[readPos], chars, writePos);
        return new String(chars);
    }

    /**
     * Encode three bytes as 4 Base64 digits.
     *
     * @param b1    The first byte.
     * @param b2    The second byte.
     * @param b3    The third byte.
     * @param chars The output character array in which the base 64 digits will be stored.
     * @param j     The position at whichthe first Base64 digit will be stored.
     */
    private void encode3(final byte b1,
                         final byte b2,
                         final byte b3,
                         final char[] chars,
                         final int j) {
        chars[j] = ENCODING[(b1 & 0xfc) >> 2];
        chars[j + 1] = ENCODING[((b1 & 0x3) << 4) | ((b2 & 0xf0) >> 4)];
        chars[j + 2] = ENCODING[((b2 & 0xf) << 2) | ((b3 & 0xc0) >> 6)];
        chars[j + 3] = ENCODING[b3 & 0x3f];
    }

    /**
     * Encode a single byte as 2 Base64 digits.
     *
     * @param b1    The input byte.
     * @param chars The output character array in which the base 64 digits will be stored.
     * @param j     The position at whichthe first Base64 digit will be stored.
     */
    private void encode1(final byte b1,
                         final char[] chars,
                         final int j) {
        chars[j] = ENCODING[(b1 & 0xfc) >> 2];
        chars[j + 1] = ENCODING[(b1 & 0x3) << 4];
    }

    /**
     * Decode a 22 Base64 digit string into 16 bytes.
     *
     * @param str The Base64 digit string.
     * @return The 16 bytes.
     */
    private byte[] decode(final String str) {
        assert str != null && str.length() == 22;
        final var bytes = new byte[16];
        var i = 0;
        var j = 0;
        do {
            decode4(bytes, i, str, j);
            i += 3;
            j += 4;
        } while (i < 15);
        decode2(bytes, i, str, j);
        return bytes;
    }

    /**
     * Decode 4 Base64 digits into 3 bytes.
     *
     * @param bytes The destination where decoded bytes will be stored.
     * @param i     The position at which to store the first decoded bytes.
     * @param str   A string of Base64 digits.
     * @param j     The position of the first Base64 digit.
     */
    private void decode4(final byte[] bytes,
                         final int i,
                         final String str,
                         final int j) {
        final var w = decode(str.charAt(j));
        final var x = decode(str.charAt(j + 1));
        final var y = decode(str.charAt(j + 2));
        final var z = decode(str.charAt(j + 3));
        bytes[i] = (byte) ((w << 2) | ((x & 0x30) >> 4));
        bytes[i + 1] = (byte) (((x & 0xf) << 4) | ((y & 0x3c) >> 2));
        bytes[i + 2] = (byte) (((y & 0x3) << 6) | z);
    }

    /**
     * Decode 2 Base64 digits into 1 byte.
     *
     * @param bytes  The destination where decoded bytes will be stored.
     * @param dest   The position at which to store the decoded byte.
     * @param string A string of Base64 digits.
     * @param src    The position of the first Base64 digit.
     */
    private void decode2(final byte[] bytes,
                         final int dest,
                         final String string,
                         final int src) {
        final var w = decode(string.charAt(src));
        final var x = decode(string.charAt(src + 1));
        bytes[dest] = (byte) ((w << 2) | ((x & 0x30) >> 4));
    }

    /**
     * Decode a single Base64 digit.
     *
     * @param ch The Base64 digit.
     * @return The value of the Base64 digit.
     */
    private int decode(final char ch) {
        assert ch <= 256;
        final var value = DECODING[ch];
        assert value != -1;
        return value;
    }

    /**
     * Extract an instant from an identifier.
     *
     * @param identifier The identifier.
     * @return The instant.
     */
    @Override
    public Instant toInstant(final Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        if (identifier instanceof UUIDIdentifier uuidIdentifier) {
            return delegate.toInstant(uuidIdentifier.binary());

        } else {
            throw new IllegalArgumentException("UUIDIdentifier is required");
        }
    }

    /**
     * Generate a lower-bound identifier for temporal value that can be used in range queries.
     *
     * @param time The temporal value ({@link java.time.Instant}, {@link java.time.LocalDate},
     *             {@link java.time.LocalDateTime}, {@link java.time.OffsetDateTime},
     *             {@link java.time.ZonedDateTime})
     * @return A lower-bound identifier that can be used in a range query.
     * @throws IllegalArgumentException If the temporal type is not supported.
     */
    @Override
    public Identifier asLowerBound(final Temporal time) {
        final var binary = delegate.fromTicks(toTicks(time, false), 0x8000000000000000L);
        final var text = encode(binary);
        return new UUIDIdentifier(text, binary);
    }

    /**
     * Generate an upper-bound identifier for temporal value that can be used in range queries.
     *
     * @param time The temporal value ({@link java.time.Instant}, {@link java.time.LocalDate},
     *             {@link java.time.LocalDateTime}, {@link java.time.OffsetDateTime},
     *             {@link java.time.ZonedDateTime})
     * @return An upper-bound identifier that can be used in a range query.
     * @throws IllegalArgumentException If the temporal type is not supported.
     */
    @Override
    public Identifier asUpperBound(final Temporal time) {
        final var binary = delegate.fromTicks(toTicks(time, true), 0x8FFFFFFFFFFFFFFFL);
        final var text = encode(binary);
        return new UUIDIdentifier(text, binary);
    }

    /**
     * Given a temporal value extract the number of UUID ticks (100 nanoseconds).
     *
     * @param temporal The temporal value ({@link java.time.Instant}, {@link java.time.LocalDate},
     *                 {@link java.time.LocalDateTime}, {@link java.time.OffsetDateTime},
     *                 {@link java.time.ZonedDateTime})
     * @return The number of ticks.
     * @throws IllegalArgumentException If the temporal type is not supported.
     */
    private long toTicks(final Temporal temporal, final boolean upper) {
        switch (temporal) {
            case Instant instant -> {
                final var adj = EPOCH_ADJ + (upper ? TICKS_PER_MILLISECOND - 1 : 0L);
                return instant.toEpochMilli() * TICKS_PER_MILLISECOND + adj;
            }
            case LocalDate date -> {
                final var time = upper ? LocalTime.of(23, 59, 59) : LocalTime.of(0, 0, 0);
                final var adj = EPOCH_ADJ + (upper ? TICKS_PER_SECOND - 1 : 0L);
                return date.toEpochSecond(time, ZoneOffset.UTC) * TICKS_PER_SECOND + adj;
            }
            case LocalDateTime dateTime -> {
                final var adj = EPOCH_ADJ + (upper ? TICKS_PER_SECOND - 1 : 0L);
                return dateTime.toEpochSecond(ZoneOffset.UTC) * TICKS_PER_SECOND + adj;
            }
            case OffsetDateTime dateTime -> {
                final var adj = EPOCH_ADJ + (upper ? TICKS_PER_SECOND - 1 : 0L);
                return dateTime.toEpochSecond() * TICKS_PER_SECOND + adj;
            }
            case ZonedDateTime dateTime -> {
                final var adj = EPOCH_ADJ + (upper ? TICKS_PER_SECOND - 1 : 0L);
                return dateTime.toEpochSecond() * TICKS_PER_SECOND + adj;
            }
            default -> throw new IllegalArgumentException(temporal.getClass().getName() + " is not supported");
        }
    }
}
