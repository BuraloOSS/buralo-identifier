/*
 *  Copyright 2022-2026 Búraló Technologies
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
package com.buralotech.oss.identifier.spring.r2dbc.postgresql;

import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.spring.IdentifierConfigProperties;
import com.buralotech.oss.identifier.spring.IdentifierConverter;
import com.buralotech.oss.identifier.spring.IdentifierJacksonModule;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import io.r2dbc.spi.Option;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Spring auto-configuration for identifier codec configuration.
 */
@Configuration
@ConditionalOnProperty(name = "buralotech.identifier.postgresql.codecs.enabled", havingValue = "true")
@ConditionalOnClass(name = "io.r2dbc.postgresql.codec.Codec")
public class IdentifierCodecConfig {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer connectionFactoryCustomizer(final IdentifierService identifierService) {
        return builder ->
                builder.option(
                        Option.valueOf("extensions"),
                        List.of((CodecRegistrar) (_, _, registry) -> {
                            registry.addFirst(new IdentifierCodec(identifierService));
                            registry.addFirst(new IdentifierArrayCodec(identifierService));
                            return Mono.empty();
                        })
                );
    }

}
