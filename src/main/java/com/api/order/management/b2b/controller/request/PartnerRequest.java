package com.api.order.management.b2b.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PartnerRequest(
        @NotBlank(message = "O nome do parceiro é obrigatório") String name,
        @NotNull(message = "O limite de crédito inicial é obrigatório")
        @Min(value = 0, message = "O limite de crédito não pode ser negativo") BigDecimal creditLimit
) {}
