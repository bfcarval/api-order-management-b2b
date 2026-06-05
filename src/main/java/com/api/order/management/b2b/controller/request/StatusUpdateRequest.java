package com.api.order.management.b2b.controller.request;

import com.api.order.management.b2b.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "O novo status deve ser informado") OrderStatus status
) {}
