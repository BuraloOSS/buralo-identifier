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
import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDVersionDelegate;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Shrinkable;
import net.jqwik.api.arbitraries.ArbitraryDecorator;

import java.util.Random;

/**
 * Implementation of arbitrary that generates valid identifiers for property based tests.
 */
public class DefaultIdentifierArbitrary
        extends ArbitraryDecorator<Identifier>
        implements IdentifierArbitrary {

    /**
     * Used to generate ids.
     */
    private final IdentifierService identifierService;

    /**
     * Initialise the arbitrary.
     *
     * @param delegate The identifier specific scheme.
     */
    public DefaultIdentifierArbitrary(final UUIDVersionDelegate delegate) {
        this.identifierService = new UUIDIdentifierService(delegate);
    }

    /**
     * Used for generating a stream of identifiers.
     *
     * @return The generator.
     */
    @Override
    protected Arbitrary<Identifier> arbitrary() {
        return Arbitraries.fromGenerator(this::generate);
    }

    /**
     * Wrap the identifier generation in a {@link Shrinkable}.
     *
     * @param random The source of randomness (ignored).
     * @return The {@link Shrinkable}.
     */
    private Shrinkable<Identifier> generate(final Random random) {
        return Shrinkable.supplyUnshrinkable(identifierService::generate);
    }
}
