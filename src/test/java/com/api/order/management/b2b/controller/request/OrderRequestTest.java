package com.api.order.management.b2b.controller.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("1. Deve passar na validação quando o pedido e seus itens forem válidos")
    void shouldPassWhenOrderAndItemsAreValid() {
        var validItem = new OrderItemRequest("Notebook Dell", 1, new BigDecimal("4500.00"));
        var request = new OrderRequest(1L, List.of(validItem));

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O request válido não deve conter violações");
    }

    @Test
    @DisplayName("2. Deve falhar quando o partnerId for nulo")
    void shouldFailWhenPartnerIdIsNull() {
        var validItem = new OrderItemRequest("Notebook Dell", 1, new BigDecimal("4500.00"));
        var request = new OrderRequest(null, List.of(validItem));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("ID do parceiro é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("3. Deve falhar quando a lista de itens for nula")
    void shouldFailWhenItemsListIsNull() {
        var request = new OrderRequest(1L, null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        assertEquals(1, violations.size());
        assertEquals("A lista de itens não pode estar vazia", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("4. Deve falhar quando a lista de itens estiver vazia")
    void shouldFailWhenItemsListIsEmpty() {
        var request = new OrderRequest(1L, Collections.emptyList());

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("A lista de itens não pode estar vazia", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("5. Deve falhar (validação em cascata) se um item dentro da lista for inválido")
    void shouldFailWhenAnItemInTheListIsInvalid() {
        var invalidItem = new OrderItemRequest("Notebook Dell", 0, new BigDecimal("4500.00"));
        var request = new OrderRequest(1L, List.of(invalidItem));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "A anotação @Valid deveria pegar o erro do item interno");
        assertEquals(1, violations.size());
        assertEquals("Mínimo de 1 item", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("6. Deve falhar com múltiplas violações se as regras do pedido e dos itens forem quebradas juntas")
    void shouldFailWithMultipleViolations() {
        var invalidItem = new OrderItemRequest("Notebook Dell", 1, new BigDecimal("-10.00"));
        var request = new OrderRequest(null, List.of(invalidItem));

        var violations = validator.validate(request);

        assertEquals(2, violations.size());

        var hasPartnerError = violations.stream().anyMatch(v -> v.getMessage().equals("ID do parceiro é obrigatório"));
        var hasItemPriceError = violations.stream().anyMatch(v -> v.getMessage().equals("Preço inválido"));

        assertTrue(hasPartnerError, "Deveria conter o erro do partnerId");
        assertTrue(hasItemPriceError, "Deveria conter o erro do preço do item");
    }
}
