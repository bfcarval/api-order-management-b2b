package com.api.order.management.b2b.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemDTOTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters tradicionais")
    void shouldGetAndSetPropertiesSuccessfully() {
        var dto = new OrderItemDTO();
        var id = 10L;
        var product = "Notebook Dell Latitude";
        var quantity = 2;
        var unitPrice = new BigDecimal("4500.00");

        dto.setId(id);
        dto.setProduct(product);
        dto.setQuantity(quantity);
        dto.setUnitPrice(unitPrice);

        assertEquals(id, dto.getId());
        assertEquals(product, dto.getProduct());
        assertEquals(quantity, dto.getQuantity());
        assertEquals(unitPrice, dto.getUnitPrice());
    }

    @Test
    @DisplayName("2. Deve construir o DTO corretamente utilizando a estrutura fluida do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var dto = OrderItemDTO.builder()
                .id(15L)
                .product("Monitor LG 29'")
                .quantity(1)
                .unitPrice(new BigDecimal("1200.00"))
                .build();

        assertNotNull(dto);
        assertEquals(15L, dto.getId());
        assertEquals("Monitor LG 29'", dto.getProduct());
        assertEquals(1, dto.getQuantity());
        assertEquals(new BigDecimal("1200.00"), dto.getUnitPrice());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias vazias usando o construtor padrão")
    void shouldSupportNoArgsConstructor() {
        var dto = new OrderItemDTO();

        assertNull(dto.getId());
        assertNull(dto.getProduct());
        assertNull(dto.getQuantity());
        assertNull(dto.getUnitPrice());
    }

    @Test
    @DisplayName("4. Deve mapear todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var dto = new OrderItemDTO(
                1L,
                "Cadeira Ergonômica",
                3,
                new BigDecimal("850.00")
        );

        assertEquals(1L, dto.getId());
        assertEquals("Cadeira Ergonômica", dto.getProduct());
        assertEquals(3, dto.getQuantity());
        assertEquals(new BigDecimal("850.00"), dto.getUnitPrice());
    }
}
