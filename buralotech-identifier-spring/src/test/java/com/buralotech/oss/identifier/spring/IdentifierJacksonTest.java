/*
 *  Copyright 2025 Búraló Technologies
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

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDVersion7Delegate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IdentifierJacksonTest {

    private ObjectMapper objectMapper;

    private static final String GOOD_ID1_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    private static final String GOOD_ID2_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    private static final byte[] GOOD_ID1_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    private static final byte[] GOOD_ID2_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    @BeforeEach
    void setup() {
        final var identifierService = new UUIDIdentifierService(new UUIDVersion7Delegate());
        objectMapper = JsonMapper.builder().addModule(new IdentifierJacksonModule(identifierService)).build();
    }

    private static Stream<Arguments> writeValue() {
        return Stream.of(
                arguments(GOOD_ID1, "\"" + GOOD_ID1_STR + "\""),
                arguments(GOOD_ID2, "\"" + GOOD_ID2_STR + "\""),
                arguments(null, "null")
        );
    }

    @ParameterizedTest
    @MethodSource
    void writeValue(final Identifier input, final String expected) throws Exception {
        assertThat(objectMapper.writeValueAsString(input)).isEqualTo(expected);
    }

    private static Stream<Arguments> writeValues() {
        return Stream.of(
                arguments(new TreeSet<>(List.of(GOOD_ID1, GOOD_ID2)), "[\"-Tk3zAmZShTpkXSCMLOF2k\",\"-Tk3zAmZUTLWhGW7ABHnNF\"]"),
                arguments(List.of(GOOD_ID1, GOOD_ID2), "[\"-Tk3zAmZShTpkXSCMLOF2k\",\"-Tk3zAmZUTLWhGW7ABHnNF\"]"),
                arguments(new TreeMap<>(Map.of(GOOD_ID1, "1", GOOD_ID2, "2")), "{\"-Tk3zAmZShTpkXSCMLOF2k\":\"1\",\"-Tk3zAmZUTLWhGW7ABHnNF\":\"2\"}")
        );
    }

    private static Stream<Arguments> readValue() {
        return Stream.of(
                arguments("\"" + GOOD_ID1_STR + "\"", GOOD_ID1),
                arguments("\"" + GOOD_ID2_STR + "\"", GOOD_ID2),
                arguments("null", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void readValue(final String input, final Identifier expected) throws IOException {
        assertThat(objectMapper.readValue(input, Identifier.class)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource
    void writeValues(final Object input, final String expected) throws IOException {
        assertThat(objectMapper.writeValueAsString(input)).isEqualTo(expected);
    }

    @Test
    void readValuesSet() throws Exception {
        assertThat(objectMapper.readValue("[\"-Tk3zAmZShTpkXSCMLOF2k\",\"-Tk3zAmZUTLWhGW7ABHnNF\"]", new TypeReference<Set<Identifier>>() {
        }))
                .containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesList() throws Exception {
        assertThat(objectMapper.readValue("[\"-Tk3zAmZShTpkXSCMLOF2k\",\"-Tk3zAmZUTLWhGW7ABHnNF\"]", new TypeReference<List<Identifier>>() {
        }))
                .containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesMapUsingJavaType() throws Exception {
        final var type = objectMapper.getTypeFactory().constructMapType(Map.class, Identifier.class, String.class);
        final Map<Identifier, String> actual = objectMapper.readValue("{\"-Tk3zAmZShTpkXSCMLOF2k\":\"1\",\"-Tk3zAmZUTLWhGW7ABHnNF\":\"2\"}", type);
        assertThat(actual).hasSize(2)
                .containsEntry(GOOD_ID1, "1")
                .containsEntry(GOOD_ID2, "2");
    }

    @Test
    void readValuesMapUsingTypeReference() throws Exception {
        final TypeReference<Map<Identifier, String>> type = new TypeReference<>() {
        };
        assertThat(objectMapper.readValue("{\"-Tk3zAmZShTpkXSCMLOF2k\":\"1\",\"-Tk3zAmZUTLWhGW7ABHnNF\":\"2\"}", type))
                .hasSize(2)
                .containsEntry(GOOD_ID1, "1")
                .containsEntry(GOOD_ID2, "2");
    }
}
