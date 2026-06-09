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
module buralotech.identifier.micronaut {
    requires buralotech.identifier.core;
    requires io.micronaut.micronaut_context;
    requires io.micronaut.micronaut_core;
    requires io.micronaut.micronaut_inject;
    requires io.micronaut.serde.micronaut_serde_api;
    requires jakarta.inject;
    requires jakarta.annotation;
    requires org.jspecify;
    exports com.buralotech.oss.identifier.micronaut;
    opens com.buralotech.oss.identifier.micronaut to  io.micronaut.micronaut_context, io.micronaut.micronaut_core, io.micronaut.micronaut_inject, io.micronaut.serde.micronaut_serde_api;
    uses io.micronaut.inject.BeanDefinitionReference;
    uses io.micronaut.inject.BeanConfiguration;
}