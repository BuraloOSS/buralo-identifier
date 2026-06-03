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
module buralotech.identifier.quarkus {
    requires com.fasterxml.jackson.databind;
    requires buralotech.identifier.core;
    requires jakarta.ws.rs;
    requires quarkus.arc;
    requires quarkus.jackson;
    requires jakarta.cdi;
    requires org.eclipse.microprofile.config;
    requires io.smallrye.config;
    requires org.jspecify;
    exports com.buralotech.oss.identifier.quarkus;
}