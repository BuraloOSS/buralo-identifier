/*
 *  Copyright 2022-2025 Búraló Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.buralotech.oss.identifier.api;

import java.util.HexFormat;

/**
 * And identifier has a textual and binary representation.
 */
public interface Identifier extends Comparable<Identifier> {

    /**
     * Get the textual representation of the identifier.
     *
     * @return The textual representation.
     */
    String text();

    /**
     * Get the binary representation of the identifier.
     *
     * @return The binary representation.
     */
    byte[] binary();

    /**
     * Get the hexadecimal representation of the identifier.
     *
     * @return The hexadecimal representation.
     */
    default String hex() {
        return HexFormat.of().formatHex(binary());
    }
}
