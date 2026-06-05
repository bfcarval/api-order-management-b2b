package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.controller.request.OrderItemRequest;
import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.dto.OrderItemDTO;
import com.api.order.management.b2b.model.OrderItemModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemMapperTest {

    @Test
    @DisplayName("1. Deve converter OrderItemDTO para OrderItemResponse perfeitamente")
    void shouldConvertFromDtoToResponse() {
        var dto = OrderItemDTO.builder()
                .id(10L)
                .product("Notebook Dell")
                .quantity(2)
                .unitPrice(new BigDecimal("4500.00"))
                .build();

        var response = OrderItemMapper.fromDTOToResponse(dto);

        assertNotNull(response);
        assertEquals(dto.getId(), response.id());
        assertEquals(dto.getProduct(), response.product());
        assertEquals(dto.getQuantity(), response.quantity());
        assertEquals(dto.getUnitPrice(), response.unitPrice());
    }

    @Test
    @DisplayName("2. Deve converter OrderItemDTO para OrderItemModel perfeitamente")
    void shouldConvertFromDtoToModel() {
        var dto = OrderItemDTO.builder()
                .id(20L)
                .product("Monitor LG")
                .quantity(1)
                .unitPrice(new BigDecimal("1200.00"))
                .build();

        var model = OrderItemMapper.fromDTOToModel(dto);

        assertNotNull(model);
        assertEquals(dto.getId(), model.getId());
        assertEquals(dto.getProduct(), model.getProduct());
        assertEquals(dto.getQuantity(), model.getQuantity());
        assertEquals(dto.getUnitPrice(), model.getUnitPrice());
    }

    @Test
    @DisplayName("3. Deve converter OrderItemModel para OrderItemDTO perfeitamente")
    void shouldConvertFromModelToDto() {
        var model = OrderItemModel.builder()
                .id(30L)
                .product("Teclado Mecânico")
                .quantity(5)
                .unitPrice(new BigDecimal("250.00"))
                .build();

        var dto = OrderItemMapper.fromModelToDTO(model);

        assertNotNull(dto);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getProduct(), dto.getProduct());
        assertEquals(model.getQuantity(), dto.getQuantity());
        assertEquals(model.getUnitPrice(), dto.getUnitPrice());
    }

    @Test
    @DisplayName("4. Deve mapear uma lista de OrderItemRequest de dentro de um OrderRequest para uma lista de OrderItemDTO")
    void shouldMapItemsFromRequestSuccessfully() {
        var itemRequest1 = new OrderItemRequest("Mouse Wireless", 10, new BigDecimal("80.00"));
        var itemRequest2 = new OrderItemRequest("Headset Gamer", 3, new BigDecimal("350.00"));
        var orderRequest = new OrderRequest(1L, List.of(itemRequest1, itemRequest2));

        var dtoList = OrderItemMapper.mapItemsFromRequest(orderRequest);

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());

        var firstDto = dtoList.get(0);
        assertNull(firstDto.getId());
        assertEquals("Mouse Wireless", firstDto.getProduct());
        assertEquals(10, firstDto.getQuantity());
        assertEquals(new BigDecimal("80.00"), firstDto.getUnitPrice());

        var secondDto = dtoList.get(1);
        assertNull(secondDto.getId());
        assertEquals("Headset Gamer", secondDto.getProduct());
        assertEquals(3, secondDto.getQuantity());
        assertEquals(new BigDecimal("350.00"), secondDto.getUnitPrice());
    }
}
