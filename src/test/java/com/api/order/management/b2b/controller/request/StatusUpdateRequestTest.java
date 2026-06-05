package com.api.order.management.b2b.controller.request;

import com.api.order.management.b2b.enums.OrderStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("1. Deve passar na validação quando um status válido for fornecido")
    void shouldPassWhenStatusIsValid() {
        var request = new StatusUpdateRequest(OrderStatus.APPROVED);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "O request com status válido não deve conter violações");
    }

    @Test
    @DisplayName("2. Deve falhar na validação quando o status for nulo")
    void shouldFailWhenStatusIsNull() {
        var request = new StatusUpdateRequest(null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "O status nulo deve disparar uma violação");
        assertEquals(1, violations.size());
        assertEquals("O novo status deve ser informado", violations.iterator().next().getMessage());
    }
}
