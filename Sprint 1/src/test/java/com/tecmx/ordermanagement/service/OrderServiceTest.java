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

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Order;
import com.tecmx.ordermanagement.model.OrderItem;
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
 * Tests for OrderService.
 *
 * GENERAL INSTRUCTIONS: - Complete each test following the TODO instructions. -
 * Use @Mock for OrderRepository and @InjectMocks for OrderService. - Each test
 * must follow the AAA pattern: Arrange → Act → Assert. - Verify that the
 * correct exceptions are thrown with assertThrows(). - Use verify() to confirm
 * interactions with the mocked repository.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Product sampleProduct;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("PROD-001", "Laptop", 999.99, 10);
        sampleOrder = new Order("ORD-001", "CUST-001");
    }

    // =========================================================================
    // createOrder tests
    // =========================================================================
    @Nested
    @DisplayName("createOrder()")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create an order successfully with valid data")
        void shouldCreateOrderSuccessfully() {
            // TODO resuelto: flujo exitoso de createOrder.
            // Arrange
            when(orderRepository.existsOrderById("ORD-001")).thenReturn(false);
            when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Order result = orderService.createOrder("ORD-001", "CUST-001");

            // Assert
            assertNotNull(result);
            assertEquals("ORD-001", result.getId());
            assertEquals("CUST-001", result.getCustomerId());
            verify(orderRepository, times(1)).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw ValidationException if orderId is null")
        void shouldThrowValidationExceptionWhenOrderIdIsNull() {
            // TODO resuelto: validar orderId nulo.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class, () -> orderService.createOrder(null, "CUST-001"));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if orderId is empty")
        void shouldThrowValidationExceptionWhenOrderIdIsEmpty() {
            // TODO resuelto: validar orderId vacio.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class, () -> orderService.createOrder("", "CUST-001"));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw ValidationException if customerId is null")
        void shouldThrowValidationExceptionWhenCustomerIdIsNull() {
            // TODO resuelto: validar customerId nulo.
            // Arrange / Act / Assert
            assertThrows(ValidationException.class, () -> orderService.createOrder("ORD-001", null));
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if an order with that ID already exists")
        void shouldThrowBusinessRuleExceptionWhenOrderAlreadyExists() {
            // TODO resuelto: validar regla de negocio por id duplicado.
            // Arrange
            when(orderRepository.existsOrderById("ORD-001")).thenReturn(true);

            // Act / Assert
            assertThrows(BusinessRuleException.class, () -> orderService.createOrder("ORD-001", "CUST-001"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }
    }

    // =========================================================================
    // addProductToOrder tests
    // =========================================================================
    @Nested
    @DisplayName("addProductToOrder()")
    class AddProductToOrderTests {

        @Test
        @DisplayName("Should add a product to the order successfully")
        void shouldAddProductToOrderSuccessfully() {
            // TODO resuelto: flujo exitoso de addProductToOrder.
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));
            when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(orderRepository.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Order result = orderService.addProductToOrder("ORD-001", "PROD-001", 3);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getItems().size());
            assertEquals(7, sampleProduct.getStockQuantity());
            verify(orderRepository, times(1)).saveOrder(any(Order.class));
            verify(orderRepository, times(1)).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the order does not exist")
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {
            // TODO resuelto: orden inexistente.
            // Arrange
            when(orderRepository.findOrderById("ORD-404")).thenReturn(Optional.empty());

            // Act / Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.addProductToOrder("ORD-404", "PROD-001", 1));
            verify(orderRepository, never()).saveOrder(any(Order.class));
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the product does not exist")
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {
            // TODO resuelto: producto inexistente.
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.findProductById("PROD-404")).thenReturn(Optional.empty());

            // Act / Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-404", 1));
            verify(orderRepository, never()).saveOrder(any(Order.class));
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ValidationException if quantity is <= 0")
        void shouldThrowValidationExceptionWhenQuantityIsInvalid() {
            // TODO resuelto: quantity invalida (0 y -1).
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));

            // Act / Assert
            assertThrows(ValidationException.class,
                () -> orderService.addProductToOrder("ORD-001", "PROD-001", 0));
            assertThrows(ValidationException.class,
                () -> orderService.addProductToOrder("ORD-001", "PROD-001", -1));
            verify(orderRepository, never()).saveOrder(any(Order.class));
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if there is insufficient stock")
        void shouldThrowBusinessRuleExceptionWhenInsufficientStock() {
            // TODO resuelto: stock insuficiente.
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.findProductById("PROD-001")).thenReturn(Optional.of(sampleProduct));

            // Act / Assert
            assertThrows(BusinessRuleException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", 15));
            verify(orderRepository, never()).saveOrder(any(Order.class));
            verify(orderRepository, never()).saveProduct(any(Product.class));
        }
    }

    // =========================================================================
    // confirmOrder tests
    // =========================================================================
    @Nested
    @DisplayName("confirmOrder()")
    class ConfirmOrderTests {

        @Test
        @DisplayName("Should confirm a valid order with items")
        void shouldConfirmOrderSuccessfully() {
            // TODO resuelto: confirmar orden valida con items.
            // Arrange
            sampleOrder.addItem(new OrderItem(sampleProduct, 1));
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Order result = orderService.confirmOrder("ORD-001");

            // Assert
            assertEquals(Order.Status.CONFIRMED, result.getStatus());
            verify(orderRepository, times(1)).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if the order is not in CREATED state")
        void shouldThrowBusinessRuleExceptionWhenOrderNotInCreatedState() {
            // TODO resuelto: estado distinto de CREATED.
            // Arrange
            sampleOrder.setStatus(Order.Status.CONFIRMED);
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));

            // Act / Assert
            assertThrows(BusinessRuleException.class, () -> orderService.confirmOrder("ORD-001"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if the order has no items")
        void shouldThrowBusinessRuleExceptionWhenOrderHasNoItems() {
            // TODO resuelto: orden sin items no se confirma.
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));

            // Act / Assert
            assertThrows(BusinessRuleException.class, () -> orderService.confirmOrder("ORD-001"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the order does not exist")
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {
            // TODO resuelto: orden inexistente en confirmOrder.
            // Arrange
            when(orderRepository.findOrderById("ORD-404")).thenReturn(Optional.empty());

            // Act / Assert
            assertThrows(ResourceNotFoundException.class, () -> orderService.confirmOrder("ORD-404"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }
    }

    // =========================================================================
    // cancelOrder tests
    // =========================================================================
    @Nested
    @DisplayName("cancelOrder()")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel an order in CREATED state")
        void shouldCancelCreatedOrder() {
            // TODO resuelto: cancelar orden en CREATED.
            // Arrange
            sampleOrder.setStatus(Order.Status.CREATED);
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Order result = orderService.cancelOrder("ORD-001");

            // Assert
            assertEquals(Order.Status.CANCELLED, result.getStatus());
            verify(orderRepository, times(1)).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should cancel an order in CONFIRMED state")
        void shouldCancelConfirmedOrder() {
            // TODO resuelto: cancelar orden en CONFIRMED.
            // Arrange
            sampleOrder.setStatus(Order.Status.CONFIRMED);
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Order result = orderService.cancelOrder("ORD-001");

            // Assert
            assertEquals(Order.Status.CANCELLED, result.getStatus());
            verify(orderRepository, times(1)).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if the order is already DELIVERED")
        void shouldThrowBusinessRuleExceptionWhenOrderIsDelivered() {
            // TODO resuelto: no cancelar orden en DELIVERED.
            // Arrange
            sampleOrder.setStatus(Order.Status.DELIVERED);
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));

            // Act / Assert
            assertThrows(BusinessRuleException.class, () -> orderService.cancelOrder("ORD-001"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException if the order is already CANCELLED")
        void shouldThrowBusinessRuleExceptionWhenOrderIsAlreadyCancelled() {
            // TODO resuelto: no cancelar orden ya CANCELLED.
            // Arrange
            sampleOrder.setStatus(Order.Status.CANCELLED);
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));

            // Act / Assert
            assertThrows(BusinessRuleException.class, () -> orderService.cancelOrder("ORD-001"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the order does not exist")
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {
            // TODO resuelto: orden inexistente en cancelOrder.
            // Arrange
            when(orderRepository.findOrderById("ORD-404")).thenReturn(Optional.empty());

            // Act / Assert
            assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder("ORD-404"));
            verify(orderRepository, never()).saveOrder(any(Order.class));
        }
    }

    // =========================================================================
    // getOrder tests
    // =========================================================================
    @Nested
    @DisplayName("getOrder()")
    class GetOrderTests {

        @Test
        @DisplayName("Should return the order when it exists")
        void shouldReturnOrderWhenFound() {
            // TODO resuelto: obtener orden existente.
            // Arrange
            when(orderRepository.findOrderById("ORD-001")).thenReturn(Optional.of(sampleOrder));

            // Act
            Order result = orderService.getOrder("ORD-001");

            // Assert
            assertNotNull(result);
            assertEquals("ORD-001", result.getId());
            verify(orderRepository, times(1)).findOrderById("ORD-001");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order does not exist")
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {
            // TODO resuelto: obtener orden inexistente.
            // Arrange
            when(orderRepository.findOrderById("ORD-404")).thenReturn(Optional.empty());

            // Act / Assert
            assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder("ORD-404"));
            verify(orderRepository, times(1)).findOrderById("ORD-404");
        }
    }
}
