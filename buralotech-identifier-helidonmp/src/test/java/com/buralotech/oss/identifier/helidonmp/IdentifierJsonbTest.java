package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import com.buralotech.oss.identifier.uuid.UUIDIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@HelidonTest
public class IdentifierJsonbTest {

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

    private static final String GOOD_ID1_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    private static final String GOOD_ID2_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    private static final byte[] GOOD_ID1_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    private static final byte[] GOOD_ID2_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    private static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    private static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    private Jsonb jsonb;

    @Inject
    private IdentifierService identifierService;

    @BeforeEach
    void setup() {
        jsonb = JsonbBuilder.create(new JsonbConfig()
                .withAdapters(new IdentifierJsonbAdapter(identifierService))
                .withSerializers(new IdentifierJsonbSerializer())
                .withDeserializers(new IdentifierJsonbDeserializer(identifierService)));
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
    void writeValue(final Identifier input,
                    final String expected) throws JsonProcessingException {
        assertThat(jsonb.toJson(input)).isEqualTo(expected);
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
    void readValue(final String input,
                   final Identifier expected) {

        assertThat(jsonb.fromJson(input, Identifier.class)).isEqualTo(expected);
    }

    private static Stream<Arguments> writeValues() {
        return Stream.of(
                arguments(Set.of(GOOD_ID1, GOOD_ID2), "[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]"),
                arguments(List.of(GOOD_ID1, GOOD_ID2), "[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]"),
                arguments(Map.of(GOOD_ID1, "1", GOOD_ID2, "2"), "{\"" + GOOD_ID1_STR + "\":\"1\",\"" + GOOD_ID2_STR + "\":\"2\"}")
        );
    }

    @ParameterizedTest
    @MethodSource
    void writeValues(final Object input,
                     final String expected)
            throws JSONException {
        final var json = jsonb.toJson(input);
        JSONAssert.assertEquals(expected, json, false);
    }

    @Test
    void readValuesSet() throws JsonProcessingException {
//        assertThat(jsonb.fromJson("[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]", new TypeReference<Set<Identifier>>() {
//        }))
//                .containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesList() throws JsonProcessingException {
//        assertThat(jsonb.fromJson("[\"" + GOOD_ID1_STR + "\",\"" + GOOD_ID2_STR + "\"]", new TypeReference<List<Identifier>>() {
//        }))
//                .containsExactlyInAnyOrder(GOOD_ID1, GOOD_ID2);
    }

    @Test
    void readValuesMapUsingJavaType() throws JsonProcessingException {
//        final var type = objectMapper.getTypeFactory().constructMapType(Map.class, Identifier.class, String.class);
//        final Map<Identifier, String> actual = jsonb.fromJson("{\"" + GOOD_ID1_STR + "\":\"1\",\"" + GOOD_ID2_STR + "\":\"2\"}", type);
//        assertThat(actual).hasSize(2)
//                .containsEntry(GOOD_ID1, "1")
//                .containsEntry(GOOD_ID2, "2");
    }

    @Test
    void readValuesMapUsingTypeReference() throws JsonProcessingException {
//        final TypeReference<Map<Identifier, String>> type = new TypeReference<>() {
//        };
//        assertThat(jsonb.fromJson("{\"" + GOOD_ID1_STR + "\":\"1\",\"" + GOOD_ID2_STR + "\":\"2\"}", type))
//                .hasSize(2)
//                .containsEntry(GOOD_ID1, "1")
//                .containsEntry(GOOD_ID2, "2");
    }

    @Test
    void writeComplexRecord() throws JsonProcessingException, JSONException {
        final var object = new IdentifierJacksonTest.ComplexRecord(
                GOOD_ID1,
                List.of(GOOD_ID1, GOOD_ID2),
                Set.of(GOOD_ID1, GOOD_ID2),
                Map.of(
                        GOOD_ID1, new IdentifierJacksonTest.NestedRecord(GOOD_ID1, "1"),
                        GOOD_ID2, new IdentifierJacksonTest.NestedRecord(GOOD_ID2, "2")));
        final var json = jsonb.toJson(object);
        JSONAssert.assertEquals(COMPLEX_JSON, json, false);
    }

    @Test
    void readComplexRecord() throws JsonProcessingException {
        assertThat(jsonb.fromJson(COMPLEX_JSON, IdentifierJacksonTest.ComplexRecord.class))
                .satisfies(object -> {
                    assertThat(object.id()).isEqualTo(GOOD_ID1);
                    assertThat(object.listOfIds()).containsExactly(GOOD_ID1, GOOD_ID2);
                    assertThat(object.setOfIds()).containsExactly(GOOD_ID1, GOOD_ID2);
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

    public record ComplexRecord(Identifier id,
                                List<Identifier> listOfIds,
                                Set<Identifier> setOfIds,
                                Map<Identifier, IdentifierJacksonTest.NestedRecord> nestedRecords) {

    }

    public record NestedRecord(Identifier id,
                               String name) {

    }
}
