package com.tecmx.ordermanagement.exception;


public class ValidationException extends OrderManagementException {

    private final String fieldName;

    public ValidationException(String message) {
        this(message, null);
    }

    public ValidationException(String message, String fieldName) {
        super(message, "VALIDATION_ERROR");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
