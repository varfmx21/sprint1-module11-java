package com.tecmx.ordermanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

/**
 * Inventory/product management service.
 *
 * Students must complete the implementation.
 */
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final OrderRepository orderRepository;

    public InventoryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " no puede ser nulo o vacío");
        }
    }

    private Product findProductOrThrow(String productId) {
        return orderRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product no encontrado: " + productId, productId));
    }

    /**
     * TODO resuelto:
     * 1. Valida id  → no nulo/vacío.
     * 2. Valida name → no nulo/vacío.
     * 3. Valida price > 0.
     * 4. Valida stockQuantity >= 0.
     * 5. Crea el Product, lo guarda y registra log INFO.
     */
    public Product registerProduct(String id, String name, double price, int stockQuantity) {
        validateNotEmpty(id, "id");
        validateNotEmpty(name, "name");

        if (price <= 0) {
            throw new ValidationException("price debe ser mayor que 0");
        }

        if (stockQuantity < 0) {
            throw new ValidationException("stockQuantity no puede ser negativo");
        }

        Product product = new Product(id, name, price, stockQuantity);
        Product saved = orderRepository.saveProduct(product);

        logger.info("Product registered: {} - {} (stock: {}, price: {})", id, name, stockQuantity, price);
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca el producto → ResourceNotFoundException si no existe.
     * 2. Valida additionalStock > 0.
     * 3. Suma el stock adicional.
     * 4. Guarda y loguea INFO.
     */
    public Product restockProduct(String productId, int additionalStock) {
        Product product = findProductOrThrow(productId);

        if (additionalStock <= 0) {
            throw new ValidationException("additionalStock debe ser mayor que 0");
        }

        int newTotal = product.getStockQuantity() + additionalStock;
        product.setStockQuantity(newTotal);

        Product saved = orderRepository.saveProduct(product);

        logger.info("Stock updated for {}: +{} → new stock: {}", productId, additionalStock, newTotal);
        return saved;
    }

    /**
     * TODO resuelto:
     * 1. Busca el producto → ResourceNotFoundException si no existe.
     * 2. Log DEBUG y retorna el stock actual.
     */
    public int checkStock(String productId) {
        Product product = findProductOrThrow(productId);

        logger.debug("Stock check for {}: {}", productId, product.getStockQuantity());
        return product.getStockQuantity();
    }
}
