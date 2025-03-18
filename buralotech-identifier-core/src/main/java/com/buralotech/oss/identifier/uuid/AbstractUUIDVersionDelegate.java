package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.UUIDUtil;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Encapsulates the logic that is specific to the standard UUID formats.
 */
public sealed abstract class AbstractUUIDVersionDelegate
        implements UUIDVersionDelegate
        permits UUIDVersion4Delegate, UUIDVersion6Delegate, UUIDVersion7Delegate {

    /**
     * The regular expression pattern used to validate identifiers.
     */
    private  final Pattern pattern;

    /**
     * Used to generate time based UUIDs.
     */
    private final NoArgGenerator generator;

    /**
     * The UUID type.
     */
    private final UUIDType type;

    /**
     * The acceptable lower-bound for variants.
     */
    private final int variantLowerBound;

    /**
     * The acceptable upper-bound for variants.
     */
    private final int variantUpperBound;

    protected AbstractUUIDVersionDelegate(final String patternString,
                                          final NoArgGenerator generator,
                                          final UUIDType type,
                                          final int variantLowerBound,
                                          final int variantUpperBound) {
        this.pattern = Pattern.compile(patternString);
        this.generator = generator;
        this.type = type;
        this.variantLowerBound = variantLowerBound;
        this.variantUpperBound = variantUpperBound;
    }

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
     * @param offset The start of the binary representation.
     * @return {@code true} if the binary representation is valid. Otherwise, {@code false}.
     */
    @Override
    public boolean isValidBinary(final byte[] binary,
                                 final int offset) {
        if (binary == null || binary.length  < offset + 16) {
            return false;
        }
        final var version = (binary[offset + 6] & 0xf0) >> 4;
        final var variant = (binary[offset + 8] & 0xf0) >> 4;
        return this.type.raw() == version && variant >= variantLowerBound && variant <= variantUpperBound;
    }

    /**
     * Check that the text representation is valid. The service has already checked that it is non-null and a valid length.
     *
     * @param text The binary representation.
     * @return {@code true} if the text representation is valid. Otherwise, {@code false}.
     */
    @Override
    public boolean isValidText(final String text) {
        return pattern.matcher(text).matches();
    }

    /**
     * Extract the timestamp from the UUID.
     *
     * @param binary The binary representation of the UUID.
     * @return The timestamp as an Instant.
     */
    @Override
    public Instant toInstant(final byte[] binary) {
        return Instant.ofEpochMilli(UUIDUtil.extractTimestamp(UUIDUtil.constructUUID(type, binary)));
    }
}
