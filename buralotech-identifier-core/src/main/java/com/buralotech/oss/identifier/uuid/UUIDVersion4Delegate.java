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
