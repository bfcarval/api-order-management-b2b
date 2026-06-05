package com.api.order.management.b2b.controller.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("1. Deve passar na validação quando todos os dados forem válidos")
    void shouldPassWhenRequestIsValid() {
        var request = new OrderItemRequest("Notebook Dell", 2, new BigDecimal("4500.00"));

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O request válido não deve conter violações de validação");
    }

    @Test
    @DisplayName("2. Deve passar na validação quando o preço unitário for exatamente zero")
    void shouldPassWhenUnitPriceIsZero() {
        var request = new OrderItemRequest("Brinde Corporativo", 1, BigDecimal.ZERO);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O preço unitário igual a zero deve ser aceito");
    }

    @Test
    @DisplayName("3. Deve falhar quando o produto for nulo")
    void shouldFailWhenProductIsNull() {
        var request = new OrderItemRequest(null, 2, new BigDecimal("150.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O produto é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("4. Deve falhar quando o produto for apenas espaços em branco ou vazio")
    void shouldFailWhenProductIsBlank() {
        var request = new OrderItemRequest("   ", 2, new BigDecimal("150.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O produto é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("5. Deve falhar quando a quantidade for nula")
    void shouldFailWhenQuantityIsNull() {
        var request = new OrderItemRequest("Monitor LG", null, new BigDecimal("1200.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("A quantidade é obrigatória", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("6. Deve falhar quando a quantidade for menor que um (zero)")
    void shouldFailWhenQuantityIsZero() {
        var request = new OrderItemRequest("Cadeira Ergonômica", 0, new BigDecimal("850.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Mínimo de 1 item", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("7. Deve falhar quando a quantidade for um número negativo")
    void shouldFailWhenQuantityIsNegative() {
        var request = new OrderItemRequest("Mouse Sem Fio", -5, new BigDecimal("99.90"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Mínimo de 1 item", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("8. Deve falhar quando o preço unitário for nulo")
    void shouldFailWhenUnitPriceIsNull() {
        var request = new OrderItemRequest("Teclado Mecânico", 10, null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O preço unitário é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("9. Deve falhar quando o preço unitário for negativo")
    void shouldFailWhenUnitPriceIsNegative() {
        var request = new OrderItemRequest("Webcam Full HD", 1, new BigDecimal("-10.50"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Preço inválido", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("10. Deve falhar com múltiplas violações quando todos os campos forem inválidos simultaneamente")
    void shouldFailWithMultipleViolationsWhenAllFieldsAreInvalid() {
        var request = new OrderItemRequest("", -1, new BigDecimal("-5.00"));

        var violations = validator.validate(request);

        assertEquals(3, violations.size());

        var hasProductError = violations.stream().anyMatch(v -> v.getMessage().equals("O produto é obrigatório"));
        var hasQuantityError = violations.stream().anyMatch(v -> v.getMessage().equals("Mínimo de 1 item"));
        var hasPriceError = violations.stream().anyMatch(v -> v.getMessage().equals("Preço inválido"));

        assertTrue(hasProductError, "Deveria conter o erro do produto");
        assertTrue(hasQuantityError, "Deveria conter o erro da quantidade");
        assertTrue(hasPriceError, "Deveria conter o erro do preço unitário");
    }
}
