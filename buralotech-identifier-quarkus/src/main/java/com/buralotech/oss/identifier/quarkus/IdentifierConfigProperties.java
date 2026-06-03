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
package com.buralotech.oss.identifier.quarkus;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;

/**
 * Accessor for the configuration properties for the library.
 */
@ConfigMapping(prefix = "buralotech.identifier")
public interface IdentifierConfigProperties {

    /**
     * Get the generator version. Valid values are: {@code "v7"}, {@code "v6"}, and {@code "v4"}.
     *
     * @return The generator version.
     */
    Optional<String> generator();
}

