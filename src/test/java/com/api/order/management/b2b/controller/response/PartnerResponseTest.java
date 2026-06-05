package com.api.order.management.b2b.controller.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartnerResponseTest {

    @Test
    @DisplayName("1. Deve instanciar e reter todos os dados do parceiro comercial perfeitamente")
    void shouldCreateAndRetainValues() {
        var id = 1L;
        var name = "Nova Distribuidora S.A.";
        var creditLimit = new BigDecimal("75000.00");

        var response = new PartnerResponse(id, name, creditLimit);

        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(creditLimit, response.creditLimit());
    }

    @Test
    @DisplayName("2. Deve respeitar os contratos de igualdade (equals) e hashCode para objetos com valores idênticos")
    void shouldRespectEqualsAndHashCodeContracts() {
        var response1 = new PartnerResponse(1L, "Parceiro Comercial B2B", new BigDecimal("50000.00"));
        var response2 = new PartnerResponse(1L, "Parceiro Comercial B2B", new BigDecimal("50000.00"));

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("3. Deve diferenciar instâncias se houver qualquer alteração nos atributos")
    void shouldDifferentiateDistinctInstances() {
        var response1 = new PartnerResponse(1L, "Parceiro A", new BigDecimal("50000.00"));
        var response2 = new PartnerResponse(1L, "Parceiro A", new BigDecimal("49000.00"));

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("4. Deve gerar a representação textual correta contendo as informações da instância")
    void shouldGenerateCorrectToStringRepresentation() {
        var response = new PartnerResponse(99L, "Atacadista Central", new BigDecimal("120000.50"));
        var toStringResult = response.toString();

        assertTrue(toStringResult.contains("id=99"));
        assertTrue(toStringResult.contains("name=Atacadista Central"));
        assertTrue(toStringResult.contains("creditLimit=120000.50"));
    }
}
