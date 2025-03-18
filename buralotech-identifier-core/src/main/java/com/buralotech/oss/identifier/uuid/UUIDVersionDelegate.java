package com.buralotech.oss.identifier.uuid;

import java.time.Instant;

/**
 * Implementations encapsulate logic that is specific to the UUID format.
 */
public interface UUIDVersionDelegate {

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier as a byte array.
     */
    byte[] generate();

    /**
     * Check that the binary representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param binary The binary representation.
     * @param offset The offset of the binary representation.
     * @return {@code true} if the binary representation is valid. Otherwise, {@code false}.
     */
    boolean isValidBinary(byte[] binary, int offset);

    /**
     * Check that the text representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param text The binary representation.
     * @return {@code true} if the text representation is valid. Otherwise, {@code false}.
     */
    boolean isValidText(String text);

    /**
     * Extract the timestamp from the UUID.
     *
     * @param binary The binary representation of the UUID.
     * @return The timestamp as an Instant.
     */
    default Instant toInstant(byte[] binary) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a UUID as a byte array from a timestamp.
     *
     * @param ticks  The timestamp in 100 nanoseconds.
     * @param suffix The second portion of the UUID.
     * @return The UUID as a byte array.
     */
    default byte[] fromTicks(long ticks, long suffix) {
        throw new UnsupportedOperationException();
    }
}
