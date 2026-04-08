package com.tecmx.ordermanagement.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Order;
import com.tecmx.ordermanagement.model.OrderItem;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

/**
 * Main order management service.
 *
 * Students must complete the implementation following the instructions in each
 * TODO. Each method must: 1. Validate inputs and throw the appropriate
 * exception. 2. Log important operations at the correct level. 3. Be testable
 * with Mockito.
 */
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " no puede ser nulo o vacío");
        }
    }

    private Order findOrderOrThrow(String orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new BusinessRuleException("Order no encontrada: " + orderId));
    }

    private Product findProductOrThrow(String productId) {
        return orderRepository.findProductById(productId)
                .orElseThrow(() -> new BusinessRuleException("Product no encontrado: " + productId));
    }

    /**
     * TODO resuelto:
     * 1. Valida orderId y customerId (no nulos/vacíos).
     * 2. Verifica que no exista ya una orden con ese ID.
     * 3. Crea la orden, la guarda y registra log INFO.
     */
    public Order createOrder(String orderId, String customerId) {
        validateNotEmpty(orderId, "orderId");
        validateNotEmpty(customerId, "customerId");

        if (orderRepository.existsOrderById(orderId)) {
            throw new BusinessRuleException("Ya existe una orden con id: " + orderId);
        }

        Order order = new Order(orderId, customerId);
        order.setStatus(Order.Status.CREATED);
        order.setItems(new ArrayList<>());

        Order saved = orderRepository.saveOrder(order);

        logger.info("Order created: {} for customer: {}", orderId, customerId);
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca la orden → ResourceNotFoundException si no existe.
     * 2. Busca el producto → ResourceNotFoundException si no existe.
     * 3. Valida quantity > 0.
     * 4. Valida stock suficiente.
     * 5. Descuenta stock, crea OrderItem, guarda ambas entidades.
     * 6. Logs INFO y DEBUG con los mensajes exactos del TODO.
     */
    public Order addProductToOrder(String orderId, String productId, int quantity) {
        Order order = findOrderOrThrow(orderId);
        Product product = findProductOrThrow(productId);

        if (quantity <= 0) {
            throw new ValidationException("quantity debe ser mayor que 0");
        }

        if (product.getStockQuantity() < quantity) {
            throw new BusinessRuleException("Stock insuficiente para producto: " + productId);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        order.addItem(new OrderItem(product, quantity));

        orderRepository.saveProduct(product);
        Order saved = orderRepository.saveOrder(order);

        logger.info("Added {}x {} to order {}", quantity, product.getName(), orderId);
        logger.debug("Remaining stock for {}: {}", productId, product.getStockQuantity());
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca la orden.
     * 2. Valida que esté en CREATED (no confirmada, no cancelada).
     * 3. Valida que tenga al menos un ítem.
     * 4. Cambia estado a CONFIRMED, guarda y loguea.
     */
    public Order confirmOrder(String orderId) {
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != Order.Status.CREATED) {
            throw new BusinessRuleException(
                    "La orden " + orderId + " no está en estado CREATED (estado actual: " + order.getStatus() + ")"
            );
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessRuleException("No se puede confirmar una orden sin productos");
        }

        order.setStatus(Order.Status.CONFIRMED);
        Order saved = orderRepository.saveOrder(order);

        logger.info("Order {} confirmed with {} items, total: {}",
                orderId, saved.getItems().size(), saved.getTotal());
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca la orden.
     * 2. Rechaza la cancelación si está SHIPPED o DELIVERED.
     * 3. BONUS: restaura el stock de cada producto.
     * 4. Cambia estado a CANCELLED, guarda y loguea WARN.
     */
    public Order cancelOrder(String orderId) {
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() == Order.Status.SHIPPED || order.getStatus() == Order.Status.DELIVERED) {
            throw new BusinessRuleException(
                    "No se puede cancelar una orden en estado: " + order.getStatus()
            );
        }

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                orderRepository.saveProduct(product);
            }
        }

        order.setStatus(Order.Status.CANCELLED);
        Order saved = orderRepository.saveOrder(order);

        logger.warn("Order {} has been cancelled", orderId);
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca la orden → ResourceNotFoundException si no existe.
     * 2. Log DEBUG y retorna.
     */
    public Order getOrder(String orderId) {
        Order order = findOrderOrThrow(orderId);

        logger.debug("Retrieved order: {}", orderId);
        return order;
    }
}
