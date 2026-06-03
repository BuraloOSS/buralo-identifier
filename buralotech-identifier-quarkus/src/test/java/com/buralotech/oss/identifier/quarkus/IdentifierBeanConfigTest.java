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

import com.buralotech.oss.identifier.api.IdentifierService;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test the {@link IdentifierConfig} bean.
 */
@QuarkusComponentTest(IdentifierConfig.class)
class IdentifierBeanConfigTest {

    @Test
    void defaultIdentifierFormat(final Instance<IdentifierService> identifierService) {
        assertIdentifierService(identifierService, 7);
    }

    @Test
    @TestConfigProperty.TestConfigProperties({
            @TestConfigProperty(key = "io.smallrye.config.Converters.empty-to-null", value = "false"),
            @TestConfigProperty(key = "buralotech.identifier.generator", value = "")})
    void emptyIdentifierFormat(final Instance<IdentifierService> identifierService) {
        assertIdentifierService(identifierService, 7);
    }

    @Test
    @TestConfigProperty(key = "buralotech.identifier.generator", value = "v7")
    void v7IdentifierFormat(final Instance<IdentifierService> identifierService) {
        assertIdentifierService(identifierService, 7);
    }

    @Test
    @TestConfigProperty(key = "buralotech.identifier.generator", value = "v6")
    void v6IdentifierFormat(final Instance<IdentifierService> identifierService) {
        assertIdentifierService(identifierService, 6);
    }

    @Test
    @TestConfigProperty(key = "buralotech.identifier.generator", value = "v4")
    void v4IdentifierFormat(final Instance<IdentifierService> identifierService) {
        assertIdentifierService(identifierService, 4);
    }

    private void assertIdentifierService(final Instance<IdentifierService> identifierService,
                                         final int uuidVersion) {
        assertThat(identifierService.isResolvable()).isTrue();
        assertThat(identifierService.get().generate().uuid().version()).isEqualTo(uuidVersion);
    }

    @Test
    void paramConverterProviderExists(final Instance<IdentifierParamConverterProvider> paramConverterProvider) {
        assertThat(paramConverterProvider.isResolvable()).isTrue();
    }

    @Test
    void objectMapperCustomizerExists(final Instance<ObjectMapperCustomizer> objectMapperCustomizer) {
        assertThat(objectMapperCustomizer.isResolvable()).isTrue();
    }
}
