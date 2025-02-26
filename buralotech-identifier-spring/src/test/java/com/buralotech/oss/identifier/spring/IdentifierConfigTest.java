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
package com.buralotech.oss.identifier.spring;

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IdentifierConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IdentifierConfig.class));

    private static Stream<Arguments> verifyBeans() {
        return Stream.of(
                arguments("v1", UUIDVersion1Delegate.class),
                arguments("v6", UUIDVersion6Delegate.class),
                arguments("v7", UUIDVersion7Delegate.class)
        );
    }

    @ParameterizedTest
    @MethodSource
    void verifyBeans(final String generatorVersion,
                     final Class<UUIDVersionDelegate> generatorClass) {
        contextRunner
                .withPropertyValues("buralotech.identifier.generator=" + generatorVersion)
                .run(context -> {
                    assertThat(context).hasSingleBean(IdentifierService.class);
                    assertThat(context).hasSingleBean(IdentifierConverter.class);
                    assertThat(context).hasSingleBean(UUIDIdentifierService.class);
                    assertThat(context).hasSingleBean(UUIDVersionDelegate.class);
                    assertThat(context).hasSingleBean(generatorClass);
                    assertThat(context).hasSingleBean(Jackson2ObjectMapperBuilderCustomizer.class);
                });
    }
}
