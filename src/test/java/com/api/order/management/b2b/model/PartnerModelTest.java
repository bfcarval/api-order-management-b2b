package com.api.order.management.b2b.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PartnerModelTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters")
    void shouldGetAndSetPropertiesSuccessfully() {
        var model = new PartnerModel();
        var id = 1L;
        var name = "Nova Distribuidora S.A.";
        var creditLimit = new BigDecimal("75000.00");

        model.setId(id);
        model.setName(name);
        model.setCreditLimit(creditLimit);

        assertEquals(id, model.getId());
        assertEquals(name, model.getName());
        assertEquals(creditLimit, model.getCreditLimit());
    }

    @Test
    @DisplayName("2. Deve construir o modelo de entidade corretamente através do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var model = PartnerModel.builder()
                .id(5L)
                .name("Atacadista Central Ltda")
                .creditLimit(new BigDecimal("120000.50"))
                .build();

        assertNotNull(model);
        assertEquals(5L, model.getId());
        assertEquals("Atacadista Central Ltda", model.getName());
        assertEquals(new BigDecimal("120000.50"), model.getCreditLimit());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias limpas usando o construtor padrão")
    void shouldSupportNoArgsConstructor() {
        var model = new PartnerModel();

        assertNull(model.getId());
        assertNull(model.getName());
        assertNull(model.getCreditLimit());
    }

    @Test
    @DisplayName("4. Deve injetar todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var model = new PartnerModel(
                10L,
                "Comércio de Alimentos Alfa",
                new BigDecimal("45000.00")
        );

        assertEquals(10L, model.getId());
        assertEquals("Comércio de Alimentos Alfa", model.getName());
        assertEquals(new BigDecimal("45000.00"), model.getCreditLimit());
    }
}
