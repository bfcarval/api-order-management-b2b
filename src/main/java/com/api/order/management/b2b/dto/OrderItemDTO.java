package com.api.order.management.b2b.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderItemDTO {

    private Long id;
    private String product;
    private Integer quantity;
    private BigDecimal unitPrice;
}
