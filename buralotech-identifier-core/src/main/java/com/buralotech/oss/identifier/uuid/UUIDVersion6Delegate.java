package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.UUIDUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Encapsulates the logic that is specific to the standard type 6 UUID format.
 */
public class UUIDVersion6Delegate implements UUIDVersionDelegate {

    /**
     * The regular expression pattern used to validate identifiers.
     */
    private static final Pattern PATTERN = Pattern.compile("[0-9a-zA-Z_-]{8}[NOPQ][0-9a-zA-Z_-][159DHLPTXaeimquy][0-9a-zA-Z_-]{10}[FVk-]");

    /**
     * Used to generate time based UUIDs.
     */
    private final NoArgGenerator generator = Generators.timeBasedReorderedGenerator();

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier as a byte array.
     */
    @Override
    public byte[] generate() {
        final var uuid = generator.generate();
        return UUIDUtil.asByteArray(uuid);
    }

    /**
     * Check that the binary representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param binary The binary representation.
     * @return {@code true} if the binary representation is valid. Otherwise, {@code false}.
     */
    @Override
    public boolean isValidBinary(final byte[] binary) {
        return ((binary[6] & 0xf0) >> 4 == 6) && ((binary[8] & 0xc0) >> 6 == 2);
    }

    /**
     * Check that the text representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param text The binary representation.
     * @return {@code true} if the text representation is valid. Otherwise, {@code false}.
     */
    @Override
    public boolean isValidText(final String text) {
        return PATTERN.matcher(text).matches();
    }

    /**
     * Extract the timestamp from the UUID.
     *
     * @param binary The binary representation of the UUID.
     * @return The timestamp as an Instant.
     */
    @Override
    public Instant toInstant(final byte[] binary) {
        return Instant.ofEpochMilli(UUIDUtil.extractTimestamp(UUIDUtil.constructUUID(UUIDType.TIME_BASED_REORDERED, binary)));
    }

    /**
     * Create a UUID as a byte array from a timestamp.
     *
     * @param ticks  The timestamp in 100 nanoseconds.
     * @param suffix The second portion of the UUID.
     * @return The UUID as a byte array.
     */
    @Override
    public byte[] fromTicks(final long ticks, final long suffix) {
        final var bytes = new byte[16];
        final var buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(((ticks & 0xFFFFFFFFFFFFF000L) << 4) | 0x06000L | (ticks & 0x0FFFL));
        buffer.putLong(suffix);
        return bytes;
    }
}
