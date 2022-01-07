package com.michelkapoko.copper.bitmex.integration.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactStatus {
    PENDING("Pending"), COMPLETED("Completed"), CONFIRMED("Confirmed"), PROCESSING("Processing");

    private final String status;

    TransactStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus(){
        return this.status;
    }
}
