package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.controller.response.OrderResponse;
import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.model.OrderModel;

public final class OrderMapper {

    public static OrderResponse fromDTOToResponse(final OrderDTO orderDTO) {
        final var itemResponses = orderDTO.getItems().stream()
                .map(OrderItemMapper::fromDTOToResponse)
                .toList();

        return new OrderResponse(
                orderDTO.getId(),
                orderDTO.getPartnerId(),
                itemResponses,
                orderDTO.getTotalValue(),
                orderDTO.getStatus(),
                orderDTO.getCreatedAt(),
                orderDTO.getUpdatedAt()
        );
    }

    public static OrderDTO fromModelToDTO(final OrderModel orderModel) {
        final var itemDTOs = orderModel.getItems().stream()
                .map(OrderItemMapper::fromModelToDTO)
                .toList();

        return OrderDTO.builder()
                .id(orderModel.getId())
                .partnerId(orderModel.getPartnerId())
                .items(itemDTOs)
                .totalValue(orderModel.getTotalValue())
                .status(orderModel.getStatus())
                .createdAt(orderModel.getCreatedAt())
                .updatedAt(orderModel.getUpdatedAt())
                .build();
    }
}
