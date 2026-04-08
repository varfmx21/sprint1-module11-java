package com.tecmx.ordermanagement.exception;


public class ResourceNotFoundException extends OrderManagementException {

    private final String resourceId;

    public ResourceNotFoundException(String message, String resourceId) {
        super(message, "RESOURCE_NOT_FOUND");
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
