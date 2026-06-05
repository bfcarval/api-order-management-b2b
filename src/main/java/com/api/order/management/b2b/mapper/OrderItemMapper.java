package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.controller.response.OrderItemResponse;
import com.api.order.management.b2b.dto.OrderItemDTO;
import com.api.order.management.b2b.model.OrderItemModel;

import java.util.List;

public final class OrderItemMapper {

    public static OrderItemResponse fromDTOToResponse(final OrderItemDTO orderItemDTO) {
        return new OrderItemResponse(
                orderItemDTO.getId(),
                orderItemDTO.getProduct(),
                orderItemDTO.getQuantity(),
                orderItemDTO.getUnitPrice()
        );
    }

    public static OrderItemModel fromDTOToModel(final OrderItemDTO orderItemDTO) {
        return OrderItemModel.builder()
                .id(orderItemDTO.getId())
                .product(orderItemDTO.getProduct())
                .quantity(orderItemDTO.getQuantity())
                .unitPrice(orderItemDTO.getUnitPrice())
                .build();
    }

    public static OrderItemDTO fromModelToDTO(final OrderItemModel orderItemModel) {
        return OrderItemDTO.builder()
                .id(orderItemModel.getId())
                .product(orderItemModel.getProduct())
                .quantity(orderItemModel.getQuantity())
                .unitPrice(orderItemModel.getUnitPrice())
                .build();
    }

    public static List<OrderItemDTO> mapItemsFromRequest(final OrderRequest orderRequest) {
        return orderRequest.items().stream()
                .map(orderItemRequest -> OrderItemDTO.builder()
                        .product(orderItemRequest.product())
                        .quantity(orderItemRequest.quantity())
                        .unitPrice(orderItemRequest.unitPrice())
                        .build())
                .toList();
    }
}
