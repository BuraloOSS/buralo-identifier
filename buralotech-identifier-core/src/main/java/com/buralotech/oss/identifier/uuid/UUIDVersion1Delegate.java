package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.impl.UUIDUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Encapsulates the logic that is specific to the modified type 1 UUID format.
 *
 * @deprecated Use {@link }UUIDVersion6Delegate} for new developments.
 */
@Deprecated(since = "1.3.0")
public class UUIDVersion1Delegate implements UUIDVersionDelegate {

    /**
     * The regular expression pattern used to validate identifiers.
     */
    private static final Pattern PATTERN = Pattern.compile("[3456][0-9a-zA-Z_-]{9}[159DHLPTXaeimquy][0-9a-zA-Z_-]{10}[FVk-]");

    /**
     * Mask to remove the UUID version.
     */
    private static final long MASK = 0x0fffffffffffffffL;

    /**
     * Number of ticks per second. TheUUID thick is 100 nanoseconds.
     */
    private static final long TICKS_PER_SECOND = 10000000L;

    /**
     * The number of nanoseconds in a UUID tick.
     */
    private static final int TICK_NANOS = 100;

    /**
     * The adjustment to apply to convert UUID epoch to Unix Epoch. The UUID epoch 15 October 1582, while the Unix
     * epoch is 1 January 1970.
     */
    private static final long EPOCH_ADJ = 122192928000000000L;

    /**
     * Used to generate time based UUIDs.
     */
    private final NoArgGenerator generator = Generators.timeBasedGenerator();

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier as a byte array.
     */
    @Override
    public byte[] generate() {
        final var uuid = generator.generate();
        final var binary = UUIDUtil.asByteArray(uuid);
        swap(binary, 4);
        swap(binary, 2);
        return binary;
    }

    /**
     * Swap the first {@code len} bytes of the array with the subsequent {@code len} bytes.
     *
     * @param bytes The byte array.
     * @param len   The number of bytes to swap.
     */
    private void swap(final byte[] bytes,
                      final int len) {
        for (int i = 0; i < len; i++) {
            bytes[i] ^= bytes[len + i];
            bytes[len + i] ^= bytes[i];
            bytes[i] ^= bytes[len + i];
        }
    }

    /**
     * Check that the binary representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param binary The binary representation.
     * @return {@code true} if the binary representation is valid. Otherwise, {@code false}.
     */
    @Override
    public boolean isValidBinary(final byte[] binary) {
        return ((binary[0] & 0xf0) >> 4 == 1) && ((binary[8] & 0xc0) >> 6 == 2);
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
        final var buf = ByteBuffer.wrap(binary);
        buf.order(ByteOrder.BIG_ENDIAN);
        final var ticks = (buf.getLong(0) & MASK) - EPOCH_ADJ;
        return Instant.ofEpochSecond(ticks / TICKS_PER_SECOND, (ticks % TICKS_PER_SECOND) * TICK_NANOS);
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
        buffer.putLong(ticks | 0x1000000000000000L);
        buffer.putLong(suffix);
        return bytes;
    }
}
