package com.buralotech.oss.identifier.uuid;

import com.buralotech.oss.identifier.api.Identifier;

class TestData {
    static final String GOOD_ID1_STR = "zf3Wy94UIuel7UXWMryeIF";

    static final String GOOD_ID2_STR = "l0bK5MCbHUmOpG_nOkbw4k";

    static final String GOOD_ID1_HEX = "feb121f8a15f4faab121f8a15f7faa4d";

    static final String GOOD_ID2_HEX = "c419d519736749fc99d519736709fc17";

    static final String GOOD_ID1_UUID_STR = "feb121f8-a15f-4faa-b121-f8a15f7faa4d";

    static final String GOOD_ID2_UUID_STR = "c419d519-7367-49fc-99d5-19736709fc17";

    static final byte[] GOOD_ID1_BIN = {-2, -79, 33, -8, -95, 95, 79, -86, -79, 33, -8, -95, 95, 127, -86, 77};

    static final byte[] GOOD_ID2_BIN = {-60, 25, -43, 25, 115, 103, 73, -4, -103, -43, 25, 115, 103, 9, -4, 23};

    static final Identifier GOOD_ID1 = new UUIDIdentifier(GOOD_ID1_STR, GOOD_ID1_BIN);

    static final Identifier GOOD_ID2 = new UUIDIdentifier(GOOD_ID2_STR, GOOD_ID2_BIN);

    static final String GOOD_ID3_STR = "6jWm2dtNNDHE_n58TPb00F";

    static final String GOOD_ID4_STR = "6jWm3hjNNxLE_n58TPb00F";

    static final String GOOD_ID3_HEX = "1ef8720e9e5860e48f97318979a9c105";

    static final String GOOD_ID4_HEX = "1ef87212dbd863d58f97318979a9c105";

    static final String GOOD_ID3_UUID_STR = "1ef8720e-9e58-60e4-8f97-318979a9c105";

    static final String GOOD_ID4_UUID_STR = "1ef87212-dbd8-63d5-8f97-318979a9c105";

    static final byte[] GOOD_ID3_BIN = {30, -8, 114, 14, -98, 88, 96, -28, -113, -105, 49, -119, 121, -87, -63, 5};

    static final byte[] GOOD_ID4_BIN = {30, -8, 114, 18, -37, -40, 99, -43, -113, -105, 49, -119, 121, -87, -63, 5};

    static final Identifier GOOD_ID3 = new UUIDIdentifier(GOOD_ID3_STR, GOOD_ID3_BIN);

    static final Identifier GOOD_ID4 = new UUIDIdentifier(GOOD_ID4_STR, GOOD_ID4_BIN);

    static final String GOOD_ID5_STR = "-Tk3zAmZShTpkXSCMLOF2k";

    static final String GOOD_ID6_STR = "-Tk3zAmZUTLWhGW7ABHnNF";

    static final String GOOD_ID5_HEX = "01ec04fcbca476d7b5c2274d5d66500f";

    static final String GOOD_ID6_HEX = "01ec04fcbca47de5a1b518482cc4b361";

    static final String GOOD_ID5_UUID_STR = "01ec04fc-bca4-76d7-b5c2-274d5d66500f";

    static final String GOOD_ID6_UUID_STR = "01ec04fc-bca4-7de5-a1b5-18482cc4b361";

    static final byte[] GOOD_ID5_BIN = {1, -20, 4, -4, -68, -92, 118, -41, -75, -62, 39, 77, 93, 102, 80, 15};

    static final byte[] GOOD_ID6_BIN = {1, -20, 4, -4, -68, -92, 125, -27, -95, -75, 24, 72, 44, -60, -77, 97};

    static final Identifier GOOD_ID5 = new UUIDIdentifier(GOOD_ID5_STR, GOOD_ID5_BIN);

    static final Identifier GOOD_ID6 = new UUIDIdentifier(GOOD_ID6_STR, GOOD_ID6_BIN);

}
