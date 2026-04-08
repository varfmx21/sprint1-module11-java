package com.tecmx.ordermanagement.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for InventoryService.
 *
 * INSTRUCTIONS: - Complete each test according to the instructions. - AAA
 * pattern: Arrange → Act → Assert. - Verify exceptions with assertThrows(). -
 * Verify interactions with verify().
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("PROD-001", "Laptop", 999.99, 10);
    }

    // =========================================================================
    // registerProduct tests
    // =========================================================================
    @Nested
    @DisplayName("registerProduct()")
    class RegisterProductTests {

        @Test
        @DisplayName("Should register a product successfully")
        void shouldRegisterProductSuccessfully() {
            // TODO resuelto: registro exitoso de producto.
            // Arrange
            when(orderRepository.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Product result = inventoryService.registerProduct("PROD-002", "Mouse", 29.99, 100);

            // Assert
            assertNotNull(result);
            assertEquals("PROD-002", result.getId());
            assertEquals("Mouse", result.getName());
            assertEquals(29.99, result.getPrice(), 0.0001);
            assertEquals(100, result.getStockQuantity());
            verify(orderRepository, times(1)).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ValidationException if id is null")
        void shouldThrowValidationExceptionWhenIdIsNull() {
            // TODO resuelto: id nulo.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct(null, "Mouse", 29.99, 100));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if id is empty")
        void shouldThrowValidationExceptionWhenIdIsEmpty() {
            // TODO resuelto: id vacio.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("", "Mouse", 29.99, 100));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if name is null")
        void shouldThrowValidationExceptionWhenNameIsNull() {
            // TODO resuelto: name nulo y vacio.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class,
                () -> inventoryService.registerProduct("PROD-002", null, 29.99, 100));
            assertThrows(ValidationException.class,
                () -> inventoryService.registerProduct("PROD-002", "", 29.99, 100));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if price is <= 0")
        void shouldThrowValidationExceptionWhenPriceIsInvalid() {
            // TODO resuelto: price invalido (0 y negativo).
            // Arrange / Act / Assert
            assertThrows(ValidationException.class,
                () -> inventoryService.registerProduct("PROD-002", "Mouse", 0, 100));
            assertThrows(ValidationException.class,
                () -> inventoryService.registerProduct("PROD-002", "Mouse", -5, 100));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if stockQuantity < 0")
        void shouldThrowValidationExceptionWhenStockIsNegative() {
            // TODO resuelto: stock negativo.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("PROD-002", "Mouse", 29.99, -1));
            verifyNoInteractions(orderRepository);
        }
    }

    // =========================================================================
    // restockProduct tests
    // =========================================================================
    @Nested
    @DisplayName("restockProduct()")
    class RestockProductTests {

        @Test
        @DisplayName("Should update the stock successfully")
        void shouldRestockSuccessfully() {
            // TODO resuelto: restock exitoso.
            // Arrange
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));
            when(orderRepository.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Product result = inventoryService.restockProduct("PROD-001", 5);

            // Assert
            assertEquals(15, result.getStockQuantity());
            verify(orderRepository, times(1)).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the product does not exist")
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {
            // TODO resuelto: producto no encontrado.
            // Arrange
            when(orderRepository.findProductById("PROD-404")).thenReturn(Optional.empty());

            // Act
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> inventoryService.restockProduct("PROD-404", 5));

            // Assert
            assertEquals("PROD-404", ex.getResourceId());
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ValidationException if additionalStock <= 0")
        void shouldThrowValidationExceptionWhenAdditionalStockIsInvalid() {
            // TODO resuelto: additionalStock invalido (0 y negativo).
            // Arrange
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));

            // Act / Assert
            assertThrows(ValidationException.class,
                () -> inventoryService.restockProduct("PROD-001", 0));
            assertThrows(ValidationException.class,
                () -> inventoryService.restockProduct("PROD-001", -1));
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }
    }

    // =========================================================================
    // checkStock tests
    // =========================================================================
    @Nested
    @DisplayName("checkStock()")
    class CheckStockTests {

        @Test
        @DisplayName("Should return the stock when the product exists")
        void shouldReturnStockWhenProductExists() {
            // TODO resuelto: consulta de stock exitosa.
            // Arrange
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));

            // Act
            int stock = inventoryService.checkStock("PROD-001");

            // Assert
            assertEquals(10, stock);
            verify(orderRepository, times(1)).findProductById("PROD-001");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when the product does not exist")
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {
            // TODO resuelto: consulta de stock con producto inexistente.
            // Arrange
            when(orderRepository.findProductById("PROD-404")).thenReturn(Optional.empty());

            // Act
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> inventoryService.checkStock("PROD-404"));

            // Assert
            assertEquals("PROD-404", ex.getResourceId());
        }
    }
}
