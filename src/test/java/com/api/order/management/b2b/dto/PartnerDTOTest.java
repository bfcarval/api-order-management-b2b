package com.api.order.management.b2b.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PartnerDTOTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters tradicionais")
    void shouldGetAndSetPropertiesSuccessfully() {
        var dto = new PartnerDTO();
        var id = 1L;
        var name = "Nova Distribuidora S.A.";
        var creditLimit = new BigDecimal("75000.00");

        dto.setId(id);
        dto.setName(name);
        dto.setCreditLimit(creditLimit);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(creditLimit, dto.getCreditLimit());
    }

    @Test
    @DisplayName("2. Deve construir o DTO corretamente utilizando a estrutura fluida do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var dto = PartnerDTO.builder()
                .id(5L)
                .name("Atacadista Central Ltda")
                .creditLimit(new BigDecimal("120000.50"))
                .build();

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Atacadista Central Ltda", dto.getName());
        assertEquals(new BigDecimal("120000.50"), dto.getCreditLimit());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias vazias usando o construtor padrão")
    void shouldSupportNoArgsConstructor() {
        var dto = new PartnerDTO();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getCreditLimit());
    }

    @Test
    @DisplayName("4. Deve mapear todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var dto = new PartnerDTO(
                10L,
                "Comércio de Alimentos Alfa",
                new BigDecimal("45000.00")
        );

        assertEquals(10L, dto.getId());
        assertEquals("Comércio de Alimentos Alfa", dto.getName());
        assertEquals(new BigDecimal("45000.00"), dto.getCreditLimit());
    }
}
