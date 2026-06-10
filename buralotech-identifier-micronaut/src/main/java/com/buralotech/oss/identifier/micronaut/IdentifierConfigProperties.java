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

import io.micronaut.context.annotation.ConfigurationProperties;
import org.jspecify.annotations.Nullable;

/**
 * Configuration properties for the identifier service.
 *
 * @param generator Specifies the UUID version - should be one of v4, v6 or v7.
 */
@ConfigurationProperties("buralotech.identifier")
public record IdentifierConfigProperties(@Nullable String generator) {
}
