package com.tecmx.ordermanagement.model;

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the model classes (Order, OrderItem).
 *
 * These tests do NOT require Mockito — they are pure unit tests.
 */
class ModelTests {

    // =========================================================================
    // OrderItem.getSubtotal() tests
    // =========================================================================
    @Test
    @DisplayName("OrderItem.getSubtotal() should correctly calculate the subtotal")
    void orderItemSubtotalShouldBeCorrect() {
        // TODO resuelto: probar subtotal para valores validos.
        // Arrange
        Product product = new Product("P-1", "Laptop", 100.0, 10);
        OrderItem item = new OrderItem(product, 3);

        // Act
        double subtotal = item.getSubtotal();

        // Assert
        assertEquals(300.0, subtotal, 0.0001);
        assertEquals(product, item.getProduct());
        assertEquals(3, item.getQuantity());
    }

    @Test
    @DisplayName("OrderItem.getSubtotal() should throw ValidationException if quantity <= 0")
    void orderItemSubtotalShouldThrowWhenQuantityInvalid() {
        // TODO resuelto: validar excepcion para quantity invalido.
        // Arrange
        Product product = new Product("P-2", "Mouse", 25.0, 20);
        OrderItem item = new OrderItem(product, 0);

        // Act
        ValidationException ex = assertThrows(ValidationException.class, item::getSubtotal);

        // Assert
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
        assertEquals("quantity", ex.getFieldName());
    }

    // =========================================================================
    // Order.getTotal() tests
    // =========================================================================
    @Test
    @DisplayName("Order.getTotal() should calculate the sum of all subtotals")
    void orderTotalShouldSumAllSubtotals() {
        // TODO resuelto: sumar subtotales de multiples items.
        // Arrange
        Order order = new Order("O-1", "C-1");
        Product product1 = new Product("P-3", "Keyboard", 50.0, 10);
        Product product2 = new Product("P-4", "Headphones", 30.0, 10);
        order.addItem(new OrderItem(product1, 2));
        order.addItem(new OrderItem(product2, 3));

        // Act
        double total = order.getTotal();

        // Assert
        assertEquals(190.0, total, 0.0001);
    }

    @Test
    @DisplayName("Order.getTotal() should throw BusinessRuleException if it has no items")
    void orderTotalShouldThrowWhenNoItems() {
        // TODO resuelto: validar regla de negocio cuando no hay items.
        // Arrange
        Order order = new Order("O-2", "C-2");

        // Act
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, order::getTotal);

        // Assert
        assertEquals("BUSINESS_RULE_VIOLATION", ex.getErrorCode());
    }

    // =========================================================================
    // Constructor and getter/setter tests
    // =========================================================================
    @Test
    @DisplayName("Order should be created with CREATED status by default")
    void orderShouldHaveCreatedStatusByDefault() {
        // TODO resuelto: validar comportamiento del constructor de Order.
        // Arrange
        Order order = new Order("O-3", "C-3");

        // Act
        Order.Status status = order.getStatus();

        // Assert
        assertEquals(Order.Status.CREATED, status);
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertEquals("O-3", order.getId());
        assertEquals("C-3", order.getCustomerId());
    }

    @Test
    @DisplayName("Product should correctly store all its fields")
    void productShouldStoreAllFields() {
        // TODO resuelto: validar constructor y getters de Product.
        // Arrange
        Product product = new Product("P-5", "Monitor", 299.99, 8);

        // Act
        String id = product.getId();
        String name = product.getName();
        double price = product.getPrice();
        int stock = product.getStockQuantity();

        // Assert
        assertEquals("P-5", id);
        assertEquals("Monitor", name);
        assertEquals(299.99, price, 0.0001);
        assertEquals(8, stock);
    }
}
