package com.michelkapoko.copper.bitmex.integration.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactType {
    TRANSFER("Transfer"), TOTAL("Total"), WITHDRAWAL("Withdrawal"), DEPOSIT("Deposit");

    private final String type;

    TransactType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType(){
        return this.type;
    }
}
