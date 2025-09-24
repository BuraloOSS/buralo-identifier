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
package com.buralotech.oss.identifier.jqwik.impl;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.jqwik.api.IdentifierParam;
import com.buralotech.oss.identifier.uuid.UUIDVersion4Delegate;
import com.buralotech.oss.identifier.uuid.UUIDVersion6Delegate;
import com.buralotech.oss.identifier.uuid.UUIDVersion7Delegate;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;

public class IdentifierArbitraryProvider implements ArbitraryProvider {

    /**
     * Used for the now deprecated modified Type 4 identifier generation,
     */
    private final static Arbitrary<Identifier> V4 = new DefaultIdentifierArbitrary(new UUIDVersion4Delegate());

    /**
     * Used for Type 6 identifier generation,
     */
    private final static Arbitrary<Identifier> V6 = new DefaultIdentifierArbitrary(new UUIDVersion6Delegate());

    /**
     * Used for Type 7 identifier generation,
     */
    private final static Arbitrary<Identifier> V7 = new DefaultIdentifierArbitrary(new UUIDVersion7Delegate());

    /**
     * Determine if the argument type is supported by this provider. The argument type must be {@link IdentifierArbitrary}.
     *
     * @param targetType The argument type.
     * @return {@code true} if this provider gan generate values for the argument type.
     */
    @Override
    public boolean canProvideFor(final TypeUsage targetType) {
        return targetType.isAssignableFrom(Identifier.class);
    }

    /**
     * Get the arbitrary for identifier generation. If the {@link IdentifierParam} annotation has been applied then
     * this can be used to specify the identifier version. Otherwise, the default is for Type 7.
     *
     * @param targetType      The type of the argument for which values are being generated.
     * @param subtypeProvider Ignored.
     * @return A set containing the arbitrary for the appropriate identifier version.
     */
    @Override
    public Set<Arbitrary<?>> provideFor(final TypeUsage targetType,
                                        final SubtypeProvider subtypeProvider) {
        return Set.of(
                targetType.findAnnotation(IdentifierParam.class)
                        .map(IdentifierParam::version)
                        .map(IdentifierArbitraryProvider::getIdentifierArbitrary)
                        .orElse(V7));
    }

    /**
     * Get the priority.
     *
     * @return Always returns 5.
     */
    @Override
    public int priority() {
        return 5;
    }

    /**
     * Get an arbitrary for a specific identifier version.
     *
     * @param version The identifier version.
     * @return The arbitrary.
     */
    private static Arbitrary<Identifier> getIdentifierArbitrary(final int version) {
        return switch (version) {
            case 4 -> V4;
            case 6 -> V6;
            default -> V7;
        };
    }
}
