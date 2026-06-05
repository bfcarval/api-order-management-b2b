package com.api.order.management.b2b.dto;

import com.api.order.management.b2b.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderDTOTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters tradicionais")
    void shouldGetAndSetPropertiesSuccessfully() {
        var dto = new OrderDTO();
        var itemDto = new OrderItemDTO(1L, "Teclado Mecânico", 2, new BigDecimal("250.00"));
        var itemsList = List.of(itemDto);
        var now = LocalDateTime.now();

        dto.setId(100L);
        dto.setPartnerId(5L);
        dto.setItems(itemsList);
        dto.setTotalValue(new BigDecimal("500.00"));
        dto.setStatus(OrderStatus.PENDING);
        dto.setCreatedAt(now.minusDays(1));
        dto.setUpdatedAt(now);

        assertEquals(100L, dto.getId());
        assertEquals(5L, dto.getPartnerId());
        assertEquals(itemsList, dto.getItems());
        assertEquals(1, dto.getItems().size());
        assertEquals(new BigDecimal("500.00"), dto.getTotalValue());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(now.minusDays(1), dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("2. Deve construir o DTO corretamente utilizando a estrutura fluida do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var itemDto = new OrderItemDTO(2L, "Mouse Gamer", 1, new BigDecimal("120.00"));
        var now = LocalDateTime.now();

        var dto = OrderDTO.builder()
                .id(200L)
                .partnerId(9L)
                .items(List.of(itemDto))
                .totalValue(new BigDecimal("120.00"))
                .status(OrderStatus.APPROVED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(dto);
        assertEquals(200L, dto.getId());
        assertEquals(9L, dto.getPartnerId());
        assertEquals(1, dto.getItems().size());
        assertEquals("Mouse Gamer", dto.getItems().get(0).getProduct());
        assertEquals(new BigDecimal("120.00"), dto.getTotalValue());
        assertEquals(OrderStatus.APPROVED, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias vazias usando o construtor padrão")
    void shouldSupportNoArgsConstructor() {
        var dto = new OrderDTO();

        assertNull(dto.getId());
        assertNull(dto.getPartnerId());
        assertNull(dto.getItems());
        assertNull(dto.getTotalValue());
        assertNull(dto.getStatus());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("4. Deve mapear todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var itemDto = new OrderItemDTO(3L, "Monitor LG 29", 1, new BigDecimal("1300.00"));
        var now = LocalDateTime.now();
        var itemsList = List.of(itemDto);
        var totalValue = new BigDecimal("1300.00");

        var dto = new OrderDTO(
                300L,
                2L,
                itemsList,
                totalValue,
                OrderStatus.CANCELED,
                now,
                now
        );

        assertEquals(300L, dto.getId());
        assertEquals(2L, dto.getPartnerId());
        assertEquals(itemsList, dto.getItems());
        assertEquals(totalValue, dto.getTotalValue());
        assertEquals(OrderStatus.CANCELED, dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
