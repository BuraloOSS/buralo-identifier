/*
 *  Copyright 2022 Búraló Technologies
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
