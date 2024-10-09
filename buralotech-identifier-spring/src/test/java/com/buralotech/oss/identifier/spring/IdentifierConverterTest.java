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
package com.buralotech.oss.identifier.spring;

import com.buralotech.oss.identifier.uuid.UUIDIdentifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IdentifierConverterTest {

    private final IdentifierConverter identifierConverter = new IdentifierConverter(new UUIDIdentifierService());

    static Stream<Arguments> conversionSuccesses() {
        return Stream.of(
                arguments("3TnesPWMmyqTHniq6DPq0F", new byte[]{17, -20, -22, -31, -88, 87, -53, -19, -98, 75, 59, -74, 28, -26, -74, 5}),
                arguments("3TnesPWMmyuTHniq6DPq0F", new byte[]{17, -20, -22, -31, -88, 87, -53, -18, -98, 75, 59, -74, 28, -26, -74, 5})
        );
    }


    @ParameterizedTest
    @MethodSource
    void conversionSuccesses(final String text, final byte[] binary) {
        final var identifier = new UUIDIdentifier(text, binary);
        assertThat(identifierConverter.convert(text)).isEqualTo(identifier);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            " ",
            "3TnesPWMmyqTHniq6DPq0",
            "3TnesPWMmyuTHniq6DPq0J",
            "ITnesPWMmyuTHniq6DPq0F",
            "3TnesPWMmyuTHniq6DPq0Fk"
    })
    void conversionFailures(final String text) {
        assertThatThrownBy(() ->identifierConverter.convert(text))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
