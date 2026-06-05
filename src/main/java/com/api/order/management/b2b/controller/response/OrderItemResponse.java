package com.api.order.management.b2b.controller.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String product,
        Integer quantity,
        BigDecimal unitPrice
) {}
