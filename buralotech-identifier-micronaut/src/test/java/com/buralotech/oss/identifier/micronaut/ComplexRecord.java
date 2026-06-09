/*
 *  Copyright 2026 Búraló Technologies
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
package com.buralotech.oss.identifier.micronaut;

import com.buralotech.oss.identifier.api.Identifier;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Complex record used for testing.
 *
 * @param id            An identifier.
 * @param listOfIds     A list of identifiers.
 * @param setOfIds      A set of identifiers.
 * @param nestedRecords A map keyed by identifiers.
 */
@Serdeable
public record ComplexRecord(Identifier id,
                            List<Identifier> listOfIds,
                            Set<Identifier> setOfIds,
                            Map<Identifier, NestedRecord> nestedRecords) {

}
