package com.buralo.identifier.api;

/**
 * Generate identifiers and parse binary and textual representations of identifiers.
 */
public interface IdentifierService {

    /**
     * Generate an identifier using an underlying UUID generator.
     *
     * @return The generated identifier.
     */
    Identifier generate();

    /**
     * Decode an identifier using its text representation.
     *
     * @param text The text representation.
     * @return The identifier.
     */
    Identifier fromText(String text);

    /**
     * Decode an identifier using its binary representation.
     *
     * @param binary The binary representation.
     * @return The identifier.
     */
    Identifier fromBinary(byte[] binary);
}
