package com.beenotice.demo.infrastructure.yaml;

public class ScenarioImportException extends RuntimeException {

    public ScenarioImportException(String message) {
        super(message);
    }

    public ScenarioImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
