package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Encapsulates the logic that is specific to the standard type 7 UUID format.
 */
public final class UUIDVersion7Delegate extends AbstractUUIDVersionDelegate {

    /**
     * Construct the delegate by configuring the abstract base class for type 7 UUIDs.
     */
    public UUIDVersion7Delegate() {
        super(
                "[0-9a-zA-Z_-]{8}[RSTU][0-9a-zA-Z_-][159DHLPTXaeimquy][0-9a-zA-Z_-]{10}[FVk-]",
                Generators.timeBasedEpochRandomGenerator(),
                UUIDType.TIME_BASED_EPOCH,
                8,
                11);
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
        buffer.putLong((((ticks - 122192928000000000L) / 10000L) << 16) | 0x07000L | (suffix & 0x0FFFL));
        buffer.putLong(suffix);
        return bytes;
    }
}
