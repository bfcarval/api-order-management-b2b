package com.api.order.management.b2b.controller.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemResponseTest {

    @Test
    @DisplayName("1. Deve instanciar e reter os valores dos atributos perfeitamente")
    void shouldCreateAndRetainValues() {
        var id = 10L;
        var product = "Notebook Dell Latitude";
        var quantity = 2;
        var unitPrice = new BigDecimal("4500.00");

        var response = new OrderItemResponse(id, product, quantity, unitPrice);

        assertEquals(id, response.id());
        assertEquals(product, response.product());
        assertEquals(quantity, response.quantity());
        assertEquals(unitPrice, response.unitPrice());
    }

    @Test
    @DisplayName("2. Deve garantir a igualdade (equals) e o mesmo hashCode para instâncias com dados idênticos")
    void shouldRespectEqualsAndHashCodeContracts() {
        var response1 = new OrderItemResponse(1L, "Monitor LG", 1, new BigDecimal("1200.00"));
        var response2 = new OrderItemResponse(1L, "Monitor LG", 1, new BigDecimal("1200.00"));

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("3. Deve diferenciar instâncias com dados distintos")
    void shouldDifferentiateDistinctInstances() {
        var response1 = new OrderItemResponse(1L, "Monitor LG", 1, new BigDecimal("1200.00"));
        var response2 = new OrderItemResponse(2L, "Monitor LG", 1, new BigDecimal("1200.00"));

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("4. Deve gerar a representação em String contendo os dados corretos")
    void shouldGenerateCorrectToStringRepresentation() {
        var response = new OrderItemResponse(5L, "Mouse", 10, new BigDecimal("50.00"));
        var toStringResult = response.toString();

        assertTrue(toStringResult.contains("id=5"));
        assertTrue(toStringResult.contains("product=Mouse"));
        assertTrue(toStringResult.contains("quantity=10"));
        assertTrue(toStringResult.contains("unitPrice=50.00"));
    }
}
