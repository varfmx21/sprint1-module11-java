package com.tecmx.ordermanagement.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the custom exception classes.
 *
 * These tests validate that exceptions are built correctly, contain the
 * expected messages and codes, and maintain the correct hierarchy.
 */
class ExceptionTests {

    @Test
    @DisplayName("OrderManagementException should store message and errorCode")
    void orderManagementExceptionShouldStoreMessageAndCode() {
        OrderManagementException ex = new OrderManagementException("test error", "TEST_CODE");
        assertEquals("test error", ex.getMessage());
        assertEquals("TEST_CODE", ex.getErrorCode());
    }

    @Test
    @DisplayName("OrderManagementException should support chained cause")
    void orderManagementExceptionShouldSupportCause() {
        RuntimeException cause = new RuntimeException("root cause");
        OrderManagementException ex = new OrderManagementException("wrapper", "WRAP", cause);
        assertEquals("wrapper", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException should be an instance of OrderManagementException")
    void resourceNotFoundShouldExtendBase() {
        // TODO resuelto: verificar herencia y errorCode esperado.
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Order not found", "O-404");

        // Act
        OrderManagementException baseException = assertInstanceOf(OrderManagementException.class, ex);

        // Assert
        assertEquals("RESOURCE_NOT_FOUND", baseException.getErrorCode());
    }

    @Test
    @DisplayName("ResourceNotFoundException should have errorCode RESOURCE_NOT_FOUND")
    void resourceNotFoundShouldHaveCorrectErrorCode() {
        // TODO resuelto: verificar errorCode y resourceId.
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Product not found", "P-404");

        // Act
        String errorCode = ex.getErrorCode();
        String resourceId = ex.getResourceId();

        // Assert
        assertEquals("RESOURCE_NOT_FOUND", errorCode);
        assertEquals("P-404", resourceId);
    }

    @Test
    @DisplayName("ValidationException should be an instance of OrderManagementException")
    void validationExceptionShouldExtendBase() {
        // TODO resuelto: verificar herencia y errorCode esperado.
        // Arrange
        ValidationException ex = new ValidationException("Invalid field", "quantity");

        // Act
        OrderManagementException baseException = assertInstanceOf(OrderManagementException.class, ex);

        // Assert
        assertEquals("VALIDATION_ERROR", baseException.getErrorCode());
    }

    @Test
    @DisplayName("ValidationException should store the fieldName")
    void validationExceptionShouldStoreFieldName() {
        // TODO resuelto: verificar fieldName con el constructor correspondiente.
        // Arrange
        ValidationException ex = new ValidationException("Invalid value", "price");

        // Act
        String fieldName = ex.getFieldName();

        // Assert
        assertEquals("price", fieldName);
    }

    @Test
    @DisplayName("BusinessRuleException should be an instance of OrderManagementException")
    void businessRuleShouldExtendBase() {
        // TODO resuelto: verificar herencia y errorCode esperado.
        // Arrange
        BusinessRuleException ex = new BusinessRuleException("Cannot confirm empty order");

        // Act
        OrderManagementException baseException = assertInstanceOf(OrderManagementException.class, ex);

        // Assert
        assertEquals("BUSINESS_RULE_VIOLATION", baseException.getErrorCode());
    }

    @Test
    @DisplayName("BusinessRuleException should support constructor with cause")
    void businessRuleShouldSupportCause() {
        // TODO resuelto: verificar constructor (message, cause).
        // Arrange
        RuntimeException cause = new RuntimeException("lower level failure");

        // Act
        BusinessRuleException ex = new BusinessRuleException("Rule violated", cause);

        // Assert
        assertEquals("Rule violated", ex.getMessage());
        assertEquals("BUSINESS_RULE_VIOLATION", ex.getErrorCode());
        assertSame(cause, ex.getCause());
    }
}
