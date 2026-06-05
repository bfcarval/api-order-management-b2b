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

class PartnerRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("1. Deve passar na validação com nome e limite de crédito válidos")
    void shouldPassWhenPartnerRequestIsValid() {
        var request = new PartnerRequest("Nova Distribuidora S.A.", new BigDecimal("75000.00"));

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O request válido não deve conter violações");
    }

    @Test
    @DisplayName("2. Deve passar na validação quando o limite de crédito for exatamente zero")
    void shouldPassWhenCreditLimitIsExactlyZero() {
        var request = new PartnerRequest("Parceiro Iniciante Ltda", BigDecimal.ZERO);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O limite de crédito igual a zero deve ser aceito");
    }

    @Test
    @DisplayName("3. Deve falhar quando o nome do parceiro for nulo")
    void shouldFailWhenNameIsNull() {
        var request = new PartnerRequest(null, new BigDecimal("5000.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O nome do parceiro é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("4. Deve falhar quando o nome do parceiro for vazio ou espaços em branco")
    void shouldFailWhenNameIsBlank() {
        var request = new PartnerRequest("    ", new BigDecimal("5000.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O nome do parceiro é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("5. Deve falhar quando o limite de crédito for nulo")
    void shouldFailWhenCreditLimitIsNull() {
        var request = new PartnerRequest("Distribuidora XYZ", null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O limite de crédito inicial é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("6. Deve falhar quando o limite de crédito for negativo")
    void shouldFailWhenCreditLimitIsNegative() {
        var request = new PartnerRequest("Parceiro de Risco S.A.", new BigDecimal("-100.00"));

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O limite de crédito não pode ser negativo", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("7. Deve falhar com múltiplos erros se ambos os campos forem inválidos simultaneamente")
    void shouldFailWithMultipleViolations() {
        var request = new PartnerRequest("", new BigDecimal("-50.00"));

        var violations = validator.validate(request);

        assertEquals(2, violations.size());

        var hasNameError = violations.stream().anyMatch(v -> v.getMessage().equals("O nome do parceiro é obrigatório"));
        var hasLimitError = violations.stream().anyMatch(v -> v.getMessage().equals("O limite de crédito não pode ser negativo"));

        assertTrue(hasNameError, "Deveria conter o erro do nome");
        assertTrue(hasLimitError, "Deveria conter o erro do limite de crédito");
    }
}
