package com.michelkapoko.copper.bitmex.integration.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static com.michelkapoko.copper.bitmex.integration.adapter.BitmexIntegrationRequestUtil.generateSignature;
import static org.junit.jupiter.api.Assertions.*;

class BitmexIntegrationRequestUtilTest {

    private static final String API_SECRET = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";

    @Test
    void generateProperSignatureWithGetAndWhenApiPathNotEncoded() {
        String actual = generateSignature(API_SECRET, HttpMethod.GET, "/api/v1/instrument", 1518064236L, "");
        String expected = "c7682d435d0cfe87c16098df34ef2eb5a549d4c5a3c2b1f0f77b8af73423bf00";
        assertEquals(expected, actual);
    }

    @Test
    void generateProperSignatureWithGetAndEncodedQueryString() {
        String actual = generateSignature(API_SECRET, HttpMethod.GET,
                "/api/v1/instrument?filter=%7B%22symbol%22%3A+%22XBTM15%22%7D", 1518064237L, "");
        String expected = "e2f422547eecb5b3cb29ade2127e21b858b235b386bfa45e1c1756eb3383919f";
        assertEquals(expected, actual);
    }

    @Test
    void generateProperSignatureWithPOSTAndABody() {
        String data = "{\"symbol\":\"XBTM15\",\"price\":219.0,\"clOrdID\":\"mm_bitmex_1a/oemUeQ4CAJZgP3fjHsA\",\"orderQty\":98}";
        String actual = generateSignature(API_SECRET, HttpMethod.POST, "/api/v1/order", 1518064238L, data);
        String expected = "1749cd2ccae4aa49048ae09f0b95110cee706e0944e6a14ad0b3a8cb45bd336b";
        assertEquals(expected, actual);
    }
}