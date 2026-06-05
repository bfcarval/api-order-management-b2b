package com.api.order.management.b2b.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemRequest(
    @NotBlank(message = "O produto é obrigatório") String product,
    @NotNull(message = "A quantidade é obrigatória") @Min(value = 1, message = "Mínimo de 1 item") Integer quantity,
    @NotNull(message = "O preço unitário é obrigatório") @Min(value = 0, message = "Preço inválido") BigDecimal unitPrice
) {}
