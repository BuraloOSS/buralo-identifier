package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDType;

/**
 * Encapsulates the logic that is specific to the standard type 4 UUID format.
 */
public final class UUIDVersion4Delegate extends AbstractUUIDVersionDelegate {

    /**
     * Construct the delegate by configuring the abstract base class for type 4 UUIDs.
     */
    public UUIDVersion4Delegate() {
        super(
                "[0-9a-zA-Z_-]{8}[FGHI][0-9a-zA-Z_-](([159DHLPTXaeimquy][0-9a-zA-Z_-])|([BCDE][-0123456789ABCDEFGHIJKLMNOPQRSTUV]))[0-9a-zA-Z_-]{9}[FVk-]",
                Generators.randomBasedGenerator(),
                UUIDType.RANDOM_BASED,
                8,
                13);
    }
}
