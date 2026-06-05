package com.api.order.management.b2b.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
    @NotNull(message = "ID do parceiro é obrigatório") Long partnerId,
    @NotEmpty(message = "A lista de itens não pode estar vazia") @Valid List<OrderItemRequest> items
) {}
