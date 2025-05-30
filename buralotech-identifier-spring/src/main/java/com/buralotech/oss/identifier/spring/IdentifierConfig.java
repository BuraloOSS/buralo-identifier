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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto-configuration for identifier generation.
 */
@Configuration
public class IdentifierConfig {

    /**
     * Create the identifier service bean.
     *
     * @return The identifier service bean.
     */
    @Bean
    IdentifierService identifierService(final UUIDVersionDelegate delegate) {
        return new UUIDIdentifierService(delegate);
    }

    /**
     * Create the identifier converter bean.
     *
     * @return The identifier converter bean.
     */
    @Bean
    IdentifierConverter identifierConverter(final IdentifierService service) {
        return new IdentifierConverter(service);
    }

    @Bean
    @ConditionalOnProperty(name = "buralotech.identifier.generator", matchIfMissing = true, havingValue = "v7")
    UUIDVersionDelegate version7Delegate() {
        return new UUIDVersion7Delegate();
    }

    @Bean
    @ConditionalOnProperty(name = "buralotech.identifier.generator", havingValue = "v6")
    UUIDVersionDelegate version6Delegate() {
        return new UUIDVersion6Delegate();
    }

    @Bean
    @ConditionalOnProperty(name = "buralotech.identifier.generator", havingValue = "v4")
    UUIDVersionDelegate version4Delegate() {
        return new UUIDVersion4Delegate();
    }

    /**
     * Constructs a customizer that applies the module containing the custom serializer and deserializers for
     * identifiers during the construction of Jackson object mappers.
     *
     * @param identifierService Required to convert textual representations to identifiers.
     * @return The customizer.
     */
    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    Jackson2ObjectMapperBuilderCustomizer identifierJacksonCustomizer(final IdentifierService identifierService) {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .modulesToInstall(modules -> modules.add(new IdentifierJacksonModule(identifierService)));
    }
}
