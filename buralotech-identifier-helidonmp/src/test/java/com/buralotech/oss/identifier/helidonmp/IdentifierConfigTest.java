/*
 * Copyright 2023-2026 Búraló Technologies
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.IdentifierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.microprofile.testing.AddBean;
import io.helidon.microprofile.testing.AddConfig;
import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@HelidonTest(resetPerTest = true)
class IdentifierConfigTest {

    @Inject
    Instance<IdentifierService> identifierService;

    @Inject
    Instance<IdentifierParamConverterProvider> identifierParamConverterProvider;

    @Test
    void defaultIdentifierFormat() {
        assertIdentifierService(7);
    }

    @Test
    @AddConfig(key = "buralotech.identifier.generator", value = "")
    void emptyIdentifierFormat() {
        assertIdentifierService(7);
    }

    @Test
    @AddConfig(key = "buralotech.identifier.generator", value = "v7")
    void v7IdentifierFormat() {
        assertIdentifierService(7);
    }

    @Test
    @AddConfig(key = "buralotech.identifier.generator", value = "v6")
    void v6IdentifierFormat() {
        assertIdentifierService(6);
    }

    @Test
    @AddConfig(key = "buralotech.identifier.generator", value = "v4")
    void v4IdentifierFormat() {
        assertIdentifierService(4);
    }

    private void assertIdentifierService(final int uuidVersion) {
        assertThat(identifierService.isResolvable()).isTrue();
        assertThat(identifierService.get().generate().uuid().version()).isEqualTo(uuidVersion);
    }

    @Test
    @Disabled
    void registeredParamConverterProvider() {
        assertThat(identifierParamConverterProvider.isResolvable()).isTrue();
    }
}
