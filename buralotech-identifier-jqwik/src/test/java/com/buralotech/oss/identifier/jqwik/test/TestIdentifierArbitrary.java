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
package com.buralotech.oss.identifier.jqwik.test;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.jqwik.api.IdentifierParam;
import com.buralotech.oss.identifier.uuid.UUIDVersion1Delegate;
import com.buralotech.oss.identifier.uuid.UUIDVersion6Delegate;
import com.buralotech.oss.identifier.uuid.UUIDVersion7Delegate;
import com.buralotech.oss.identifier.uuid.UUIDVersionDelegate;
import net.jqwik.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Group
@PropertyDefaults(tries = 10)
class TestIdentifierArbitrary {

    private static final UUIDVersionDelegate V7 = new UUIDVersion7Delegate();

    private static final UUIDVersionDelegate V6 = new UUIDVersion6Delegate();

    private static final UUIDVersionDelegate V1 = new UUIDVersion1Delegate();

    @Property
    void withoutAnnotation(@ForAll Identifier id) {
        assertThat(V7.isValidText(id.text())).isTrue();
    }

    @Property
    void withAnnotationDefault(@ForAll @IdentifierParam Identifier id) {
        assertThat(V7.isValidText(id.text())).isTrue();
    }

    @Property
    void withAnnotationV7(@ForAll @IdentifierParam(version = 7) Identifier id) {
        assertThat(V7.isValidText(id.text())).isTrue();
    }

    @Property
    void withAnnotationV6(@ForAll @IdentifierParam(version = 6) Identifier id) {
        assertThat(V6.isValidText(id.text())).isTrue();
    }

    @Property
    void withAnnotationV1(@ForAll @IdentifierParam(version = 1) Identifier id) {
        assertThat(V1.isValidText(id.text())).isTrue();
    }

    @Property
    void forSet(@ForAll Set<Identifier> ids) {
        for (var id : ids) {
            assertThat(V7.isValidText(id.text())).isTrue();
        }
    }

    @Property
    void forList(@ForAll List<Identifier> ids) {
        for (var id : ids) {
            assertThat(V7.isValidText(id.text())).isTrue();
        }
    }

    @Property
    void forMapKeyedById(@ForAll Map<Identifier, String> keyedById) {
        for (var id : keyedById.keySet()) {
            assertThat(V7.isValidText(id.text())).isTrue();
        }
    }

    @Property
    void forMapWithIdValues(@ForAll Map<String, Identifier> idValues) {
        for (var id : idValues.values()) {
            assertThat(V7.isValidText(id.text())).isTrue();
        }
    }

    @Property
    void forMapIdToId(@ForAll Map<Identifier, Identifier> id2id) {
        for (var id : id2id.entrySet()) {
            assertThat(V7.isValidText(id.getKey().text())).isTrue();
            assertThat(V7.isValidText(id.getValue().text())).isTrue();
        }
    }
}
