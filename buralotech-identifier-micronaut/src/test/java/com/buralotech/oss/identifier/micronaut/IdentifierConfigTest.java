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

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * The bean factory unit tests.
 */
class IdentifierConfigTest {

    private final ApplicationContextBuilder applicationContextBuilder = ApplicationContext.builder()
            .environments("test")
            .packages("com.buralotech.oss.identifier.micronaut");

    private static Stream<Arguments> verifyBeans() {
        return Stream.of(
                arguments(UUIDIdentifierService.V4, 4),
                arguments(UUIDIdentifierService.V6, 6),
                arguments(UUIDIdentifierService.V7, 7),
                arguments("", 7),
                arguments(null, 7)
        );
    }

    @ParameterizedTest
    @MethodSource
    void verifyBeans(final String generatorVersion,
                     final int uuidVersion) {
        try (final var applicationContext = (generatorVersion == null
                ? applicationContextBuilder
                : applicationContextBuilder.properties(Map.of("buralotech.identifier.generator", generatorVersion)))
                .start()) {
            assertThat(applicationContext.findBean(IdentifierService.class))
                    .hasValueSatisfying(identifierService -> {
                        assertThat(identifierService).isInstanceOf(UUIDIdentifierService.class);
                        final var id = identifierService.generate();
                        assertThat(id.uuid().version()).isEqualTo(uuidVersion);
                    });
            assertThat(applicationContext.containsBean(IdentifierConverter.class)).isTrue();
            assertThat(applicationContext.containsBean(IdentifierSerde.class)).isTrue();
        }
    }
}
