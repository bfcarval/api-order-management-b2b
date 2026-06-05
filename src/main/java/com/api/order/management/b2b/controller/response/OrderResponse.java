package com.api.order.management.b2b.controller.response;

import com.api.order.management.b2b.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long partnerId,
        List<OrderItemResponse> items,
        BigDecimal totalValue,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
