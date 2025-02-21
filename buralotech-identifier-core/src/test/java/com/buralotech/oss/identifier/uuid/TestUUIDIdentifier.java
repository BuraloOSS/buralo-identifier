package com.buralotech.oss.identifier.uuid;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestUUIDIdentifier {

    static Stream<Arguments> verifyConversions() {
        return Stream.of(
                arguments("-OJaj5oySuTKEYn8Bd8ZuF", "019526bc6d3e77a7953e3cc9329264e9", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x3e, (byte) 0x77, (byte) 0xa7, (byte) 0x95, (byte) 0x3e, (byte) 0x3c, (byte) 0xc9, (byte) 0x32, (byte) 0x92, (byte) 0x64, (byte) 0xe9}),
                arguments("-OJaj5p-Rs57G9K_hkm2k-", "019526bc6d4073818844a565b70c83c0", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x40, (byte) 0x73, (byte) 0x81, (byte) 0x88, (byte) 0x44, (byte) 0xa5, (byte) 0x65, (byte) 0xb7, (byte) 0x0c, (byte) 0x83, (byte) 0xc0}),
                arguments("-OJaj5p-SD5ToIofXVDrw-", "019526bc6d4074e19ed13d2b8a03b7f0", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x40, (byte) 0x74, (byte) 0xe1, (byte) 0x9e, (byte) 0xd1, (byte) 0x3d, (byte) 0x2b, (byte) 0x8a, (byte) 0x03, (byte) 0xb7, (byte) 0xf0}),
                arguments("-OJaj5p-RbuCGxsdap-99-", "019526bc6d40727e8d47de299b500a28", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x40, (byte) 0x72, (byte) 0x7e, (byte) 0x8d, (byte) 0x47, (byte) 0xde, (byte) 0x29, (byte) 0x9b, (byte) 0x50, (byte) 0x0a, (byte) 0x28}),
                arguments("-OJaj5p-Uaea7oTt3ytifF", "019526bc6d407e6aa62347b913ee6ead", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x40, (byte) 0x7e, (byte) 0x6a, (byte) 0xa6, (byte) 0x23, (byte) 0x47, (byte) 0xb9, (byte) 0x13, (byte) 0xee, (byte) 0x6e, (byte) 0xad}),
                arguments("-OJaj5p-RRuNVVedNhU4XF", "019526bc6d4071ce98820aa962d7c589", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x40, (byte) 0x71, (byte) 0xce, (byte) 0x98, (byte) 0x82, (byte) 0x0a, (byte) 0xa9, (byte) 0x62, (byte) 0xd7, (byte) 0xc5, (byte) 0x89}),
                arguments("-OJaj5p0RaePGSOU5ZVDNV", "019526bc6d41726a9a45d65f1a480e62", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x41, (byte) 0x72, (byte) 0x6a, (byte) 0x9a, (byte) 0x45, (byte) 0xd6, (byte) 0x5f, (byte) 0x1a, (byte) 0x48, (byte) 0x0e, (byte) 0x62}),
                arguments("-OJaj5p0SXPmaGUJ_M_PWk", "019526bc6d417626b29917d495795a87", new byte[]{(byte) 0x01, (byte) 0x95, (byte) 0x26, (byte) 0xbc, (byte) 0x6d, (byte) 0x41, (byte) 0x76, (byte) 0x26, (byte) 0xb2, (byte) 0x99, (byte) 0x17, (byte) 0xd4, (byte) 0x95, (byte) 0x79, (byte) 0x5a, (byte) 0x87}));
    }

    @ParameterizedTest
    @MethodSource
    void verifyConversions(final String text,
                           final String hexString,
                           final byte[] binary) {
        final var lhs = new UUIDIdentifier(text, hexString);
        assertThat(lhs.text()).isEqualTo(text);
        assertThat(lhs.hex()).isEqualTo(hexString);
        assertThat(lhs.binary()).isEqualTo(binary);

        final var rhs = new UUIDIdentifier(text, binary);
        assertThat(rhs.text()).isEqualTo(text);
        assertThat(rhs.hex()).isEqualTo(hexString);
        assertThat(rhs.binary()).isEqualTo(binary);

        assertThat(lhs).isEqualTo(rhs);
    }
}
