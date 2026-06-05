package com.api.order.management.b2b.controller.response;

import com.api.order.management.b2b.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderResponseTest {

    @Test
    @DisplayName("1. Deve instanciar e expor todos os dados do pedido perfeitamente")
    void shouldCreateAndRetainValues() {
        var itemId = 10L;
        var itemResponse = new OrderItemResponse(itemId, "Notebook Dell", 2, new BigDecimal("4500.00"));
        var items = List.of(itemResponse);

        var id = 100L;
        var partnerId = 1L;
        var totalValue = new BigDecimal("9000.00");
        var status = OrderStatus.PENDING;
        var createdAt = LocalDateTime.now().minusDays(1);
        var updatedAt = LocalDateTime.now();

        var response = new OrderResponse(id, partnerId, items, totalValue, status, createdAt, updatedAt);

        assertEquals(id, response.id());
        assertEquals(partnerId, response.partnerId());
        assertEquals(items, response.items());
        assertEquals(1, response.items().size());
        assertEquals(totalValue, response.totalValue());
        assertEquals(status, response.status());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    @DisplayName("2. Deve passar no contrato equals e hashCode para objetos com valores idênticos")
    void shouldRespectEqualsAndHashCodeContracts() {
        var baseTime = LocalDateTime.of(2026, 6, 5, 12, 0);
        var item = new OrderItemResponse(1L, "Teclado", 1, new BigDecimal("150.00"));

        var response1 = new OrderResponse(100L, 1L, List.of(item), new BigDecimal("150.00"), OrderStatus.APPROVED, baseTime, baseTime);
        var response2 = new OrderResponse(100L, 1L, List.of(item), new BigDecimal("150.00"), OrderStatus.APPROVED, baseTime, baseTime);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("3. Deve diferenciar instâncias se houver qualquer alteração nos atributos")
    void shouldDifferentiateDistinctInstances() {
        var baseTime = LocalDateTime.of(2026, 6, 5, 12, 0);

        var response1 = new OrderResponse(100L, 1L, Collections.emptyList(), BigDecimal.ZERO, OrderStatus.PENDING, baseTime, baseTime);
        var response2 = new OrderResponse(100L, 1L, Collections.emptyList(), BigDecimal.ZERO, OrderStatus.CANCELED, baseTime, baseTime);

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("4. Deve conter os dados principais formatados na saída do toString")
    void shouldGenerateCorrectToStringRepresentation() {
        var baseTime = LocalDateTime.of(2026, 6, 5, 12, 0);
        var response = new OrderResponse(500L, 9L, Collections.emptyList(), new BigDecimal("1250.50"), OrderStatus.APPROVED, baseTime, baseTime);

        var toStringResult = response.toString();

        assertTrue(toStringResult.contains("id=500"));
        assertTrue(toStringResult.contains("partnerId=9"));
        assertTrue(toStringResult.contains("totalValue=1250.50"));
        assertTrue(toStringResult.contains("status=APPROVED"));
    }
}
