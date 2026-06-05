package com.api.order.management.b2b.controller.response;

import java.math.BigDecimal;

public record PartnerResponse(
        Long id,
        String name,
        BigDecimal creditLimit
) {}
