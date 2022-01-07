package com.michelkapoko.copper.bitmex.integration.exceptions;

import com.michelkapoko.copper.bitmex.integration.adapter.BitmexIntegrationRequestUtil;
import org.springframework.http.HttpMethod;

/**
 * Exception thrown when a problem is encountered during the generation of a specific signature request.
 * @see BitmexIntegrationRequestUtil#generateSignature(String, HttpMethod, String, Long, String)
 */
public class SecretSignatureException extends RuntimeException {
    public SecretSignatureException(String message) {
        super(message);
    }
}
