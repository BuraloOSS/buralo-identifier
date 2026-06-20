/*
 * Copyright 2023-2026 Búraló Technologies
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buralotech.oss.identifier.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Encapsulates the logic that is specific to the standard type 6 UUID format.
 */
public final class UUIDVersion6Delegate extends AbstractUUIDVersionDelegate {

    /**
     * Construct the delegate by configuring the abstract base class for type 6 UUIDs.
     */
    public UUIDVersion6Delegate() {
        super(
                "[0-9a-zA-Z_-]{8}[NOPQ][0-9a-zA-Z_-][159DHLPTXaeimquy][0-9a-zA-Z_-]{10}[FVk-]",
                Generators.timeBasedReorderedGenerator(),
                UUIDType.TIME_BASED_REORDERED,
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
        buffer.putLong(((ticks & 0xFFFFFFFFFFFFF000L) << 4) | 0x06000L | (ticks & 0x0FFFL));
        buffer.putLong(suffix);
        return bytes;
    }
}
