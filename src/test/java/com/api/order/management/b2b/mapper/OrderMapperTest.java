package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.controller.response.OrderItemResponse;
import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.dto.OrderItemDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.model.OrderItemModel;
import com.api.order.management.b2b.model.OrderModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class OrderMapperTest {

    @Test
    @DisplayName("1. Deve converter OrderDTO para OrderResponse isolando o sub-mapeamento de itens")
    void shouldConvertFromDtoToResponse() {
        var now = LocalDateTime.now();
        var itemDto = OrderItemDTO.builder().id(10L).product("Produto Teste").build();

        var dto = OrderDTO.builder()
                .id(1L)
                .partnerId(100L)
                .items(List.of(itemDto))
                .totalValue(new BigDecimal("500.00"))
                .status(OrderStatus.PENDING)
                .createdAt(now.minusHours(1))
                .updatedAt(now)
                .build();

        var expectedItemResponse = new OrderItemResponse(10L, "Produto Teste", 1, BigDecimal.TEN);

        try (MockedStatic<OrderItemMapper> itemMapperMock = mockStatic(OrderItemMapper.class)) {
            itemMapperMock.when(() -> OrderItemMapper.fromDTOToResponse(any(OrderItemDTO.class)))
                    .thenReturn(expectedItemResponse);

            var response = OrderMapper.fromDTOToResponse(dto);

            assertNotNull(response);
            assertEquals(dto.getId(), response.id());
            assertEquals(dto.getPartnerId(), response.partnerId());
            assertEquals(dto.getTotalValue(), response.totalValue());
            assertEquals(dto.getStatus(), response.status());
            assertEquals(dto.getCreatedAt(), response.createdAt());
            assertEquals(dto.getUpdatedAt(), response.updatedAt());

            assertEquals(1, response.items().size());
            assertEquals(expectedItemResponse, response.items().get(0));
        }
    }

    @Test
    @DisplayName("2. Deve converter OrderModel para OrderDTO isolando o sub-mapeamento de itens")
    void shouldConvertFromModelToDto() {
        var now = LocalDateTime.now();
        var itemModel = OrderItemModel.builder().id(20L).product("Modelo Teste").build();

        var model = OrderModel.builder()
                .id(2L)
                .partnerId(200L)
                .items(List.of(itemModel))
                .totalValue(new BigDecimal("1200.50"))
                .status(OrderStatus.APPROVED)
                .createdAt(now.minusDays(2))
                .updatedAt(now)
                .build();

        var expectedItemDto = OrderItemDTO.builder().id(20L).product("Modelo Teste").build();

        try (MockedStatic<OrderItemMapper> itemMapperMock = mockStatic(OrderItemMapper.class)) {
            itemMapperMock.when(() -> OrderItemMapper.fromModelToDTO(any(OrderItemModel.class)))
                    .thenReturn(expectedItemDto);

            var dto = OrderMapper.fromModelToDTO(model);

            assertNotNull(dto);
            assertEquals(model.getId(), dto.getId());
            assertEquals(model.getPartnerId(), dto.getPartnerId());
            assertEquals(model.getTotalValue(), dto.getTotalValue());
            assertEquals(model.getStatus(), dto.getStatus());
            assertEquals(model.getCreatedAt(), dto.getCreatedAt());
            assertEquals(model.getUpdatedAt(), dto.getUpdatedAt());

            assertEquals(1, dto.getItems().size());
            assertEquals(expectedItemDto, dto.getItems().get(0));
        }
    }
}
