package com.api.order.management.b2b.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemModelTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters")
    void shouldGetAndSetPropertiesSuccessfully() {
        var model = new OrderItemModel();
        var id = 1L;
        var product = "Parafuso Sextavado B2B";
        var quantity = 500;
        var unitPrice = new BigDecimal("0.75");

        model.setId(id);
        model.setProduct(product);
        model.setQuantity(quantity);
        model.setUnitPrice(unitPrice);

        assertEquals(id, model.getId());
        assertEquals(product, model.getProduct());
        assertEquals(quantity, model.getQuantity());
        assertEquals(unitPrice, model.getUnitPrice());
    }

    @Test
    @DisplayName("2. Deve construir o modelo de entidade corretamente através do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var model = OrderItemModel.builder()
                .id(10L)
                .product("Cabo de Rede Cat6 de Cobre (Rolo)")
                .quantity(5)
                .unitPrice(new BigDecimal("350.00"))
                .build();

        assertNotNull(model);
        assertEquals(10L, model.getId());
        assertEquals("Cabo de Rede Cat6 de Cobre (Rolo)", model.getProduct());
        assertEquals(5, model.getQuantity());
        assertEquals(new BigDecimal("350.00"), model.getUnitPrice());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias limpas usando o construtor padrão")
    void shouldSupportNoArgsConstructor() {
        var model = new OrderItemModel();

        assertNull(model.getId());
        assertNull(model.getProduct());
        assertNull(model.getQuantity());
        assertNull(model.getUnitPrice());
    }

    @Test
    @DisplayName("4. Deve injetar todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var model = new OrderItemModel(
                100L,
                "Placa de Vídeo RTX 4060",
                12,
                new BigDecimal("2100.00")
        );

        assertEquals(100L, model.getId());
        assertEquals("Placa de Vídeo RTX 4060", model.getProduct());
        assertEquals(12, model.getQuantity());
        assertEquals(new BigDecimal("2100.00"), model.getUnitPrice());
    }
}
