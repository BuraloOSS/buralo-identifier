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
package com.buralotech.oss.identifier.micronaut;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.uuid.UUIDIdentifier;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@MicronautTest(startApplication = false)
class IdentifierSerdeTest {

    public static final String COMPLEX_JSON = """
            {
                "id": "-Tk3zAmZShTpkXSCMLOF2k",
                "listOfIds": ["-Tk3zAmZShTpkXSCMLOF2k","-Tk3zAmZUTLWhGW7ABHnNF"],
                "setOfIds": ["-Tk3zAmZUTLWhGW7ABHnNF","-Tk3zAmZShTpkXSCMLOF2k"],
                "nestedRecords": {
                    "-Tk3zAmZShTpkXSCMLOF2k": {
                        "id": "-Tk3zAmZShTpkXSCMLOF2k",
                        "name": "1"
                    },
                    "-Tk3zAmZUTLWhGW7ABHnNF": {
                        "id": "-Tk3zAmZUTLWhGW7ABHnNF",
                        "name": "2"
                    }
                }
            }
            """;

    @Inject
    private ObjectMapper objectMapper;

    private static final String GOOD_ID1_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    private static final String GOOD_ID2_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    private static final byte[] GOOD_ID1_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    private static final byte[] GOOD_ID2_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private static Stream<Arguments> writeValue() {
        return Stream.of(
                arguments(GOOD_ID1, "\"" + GOOD_ID1_STR + "\""),
                arguments(GOOD_ID2, "\"" + GOOD_ID2_STR + "\"")
        );
    }

    @ParameterizedTest
    @MethodSource
    void writeValue(final Identifier input, final String expected) throws IOException {
        assertThat(objectMapper.writeValueAsString(input)).isEqualTo(expected);
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

    @Test
    void writeList() throws IOException, JSONException {
        JSONAssert.assertEquals(
                objectMapper.writeValueAsString(List.of(GOOD_ID1, GOOD_ID2)),
                "[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]",
                false);
    }

    @Test
    void writeSet() throws IOException, JSONException {
        JSONAssert.assertEquals(
                objectMapper.writeValueAsString(Set.of(GOOD_ID1, GOOD_ID2)),
                "[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]",
                false);
    }

    @Test
    void writeMap() throws IOException, JSONException {
        JSONAssert.assertEquals(
                objectMapper.writeValueAsString(Map.of(GOOD_ID1, "1", GOOD_ID2, "2")),
                "{\"" + GOOD_ID1_STR + "\":\"1\",\"" + GOOD_ID2_STR + "\":\"2\"}",
                false);
    }

    @Test
    void readValuesSet() throws IOException {
        assertThat(objectMapper.readValue("[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]", Argument.listOf(Identifier.class)))
                .containsExactly(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesList() throws Exception {
        assertThat(objectMapper.readValue("[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]", Argument.setOf(Identifier.class)))
                .containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesMapUsingJavaType() throws IOException {
        assertThat(objectMapper.readValue("{\"" + GOOD_ID1_STR + "\":\"1\",\"" + GOOD_ID2_STR + "\":\"2\"}", Argument.mapOf(Identifier.class, String.class)))
                .hasSize(2)
                .containsEntry(GOOD_ID1, "1")
                .containsEntry(GOOD_ID2, "2");
    }

    @Test
    void writeComplexRecord() throws IOException, JSONException {
        final var object = new ComplexRecord(
                GOOD_ID1,
                List.of(GOOD_ID1, GOOD_ID2),
                Set.of(GOOD_ID1, GOOD_ID2),
                Map.of(
                        GOOD_ID1, new NestedRecord(GOOD_ID1, "1"),
                        GOOD_ID2, new NestedRecord(GOOD_ID2, "2")));
        JSONAssert.assertEquals(objectMapper.writeValueAsString(object), COMPLEX_JSON, false);
    }

    @Test
    void readComplexRecord() throws IOException {
        assertThat(objectMapper.readValue(COMPLEX_JSON, ComplexRecord.class))
                .satisfies(object -> {
                    assertThat(object.id()).isEqualTo(GOOD_ID1);
                    assertThat(object.listOfIds()).containsExactly(GOOD_ID1, GOOD_ID2);
                    assertThat(object.setOfIds()).containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
                    assertThat(object.nestedRecords())
                            .hasSize(2)
                            .hasEntrySatisfying(GOOD_ID1, entry -> {
                                assertThat(entry.id()).isEqualTo(GOOD_ID1);
                                assertThat(entry.name()).isEqualTo("1");
                            })
                            .hasEntrySatisfying(GOOD_ID2, entry -> {
                                assertThat(entry.id()).isEqualTo(GOOD_ID2);
                                assertThat(entry.name()).isEqualTo("2");
                            });
                });
    }
}
