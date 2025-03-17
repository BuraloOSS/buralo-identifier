package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.impl.UUIDUtil;

import java.util.regex.Pattern;

/**
 * Encapsulates the logic that is specific to the standard type 4 UUID format.
 */
public class UUIDVersion4Delegate implements UUIDVersionDelegate {

    /**
     * The regular expression pattern used to validate identifiers.
     */
    private static final Pattern PATTERN = Pattern.compile("[0-9a-zA-Z_-]{8}[FGHI][0-9a-zA-Z_-](([159DHLPTXaeimquy][0-9a-zA-Z_-])|([BCDE][-0123456789ABCDEFGHIJKLMNOPQRSTUV]))[0-9a-zA-Z_-]{9}[FVk-]");

    /**
     * Used to generate time based UUIDs.
     */
    private final NoArgGenerator generator = Generators.randomBasedGenerator();

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
        final var version = (binary[6] & 0xf0) >> 4;
        final var variant = (binary[8] & 0xf0) >> 4;
        return version == 4 && variant >= 8 && variant <= 12;
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
}
