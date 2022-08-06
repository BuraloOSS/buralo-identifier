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
package com.buralo.identifier.spring;

import com.buralo.identifier.api.IdentifierService;
import com.buralo.identifier.uuid.UUIDIdentifierService;
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
    IdentifierService identifierService() {
        return new UUIDIdentifierService();
    }

    /**
     * Create the identifier converter bean.
     *
     * @return The identifier converter bean.
     */
    @Bean
    IdentifierConverter identifierConverter() {
        return new IdentifierConverter(identifierService());
    }
}
