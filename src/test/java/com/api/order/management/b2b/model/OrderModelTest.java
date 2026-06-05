package com.api.order.management.b2b.model;

import com.api.order.management.b2b.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderModelTest {

    @Test
    @DisplayName("1. Deve reter e expor os dados perfeitamente via getters e setters")
    void shouldGetAndSetPropertiesSuccessfully() {
        var model = new OrderModel();
        var item = OrderItemModel.builder().product("Produto A").build();
        var itemsList = List.of(item);
        var now = LocalDateTime.now();

        model.setId(500L);
        model.setPartnerId(10L);
        model.setItems(itemsList);
        model.setTotalValue(new BigDecimal("2500.50"));
        model.setStatus(OrderStatus.PENDING);
        model.setCreatedAt(now.minusDays(1));
        model.setUpdatedAt(now);

        assertEquals(500L, model.getId());
        assertEquals(10L, model.getPartnerId());
        assertEquals(itemsList, model.getItems());
        assertEquals(1, model.getItems().size());
        assertEquals(new BigDecimal("2500.50"), model.getTotalValue());
        assertEquals(OrderStatus.PENDING, model.getStatus());
        assertEquals(now.minusDays(1), model.getCreatedAt());
        assertEquals(now, model.getUpdatedAt());
    }

    @Test
    @DisplayName("2. Deve construir o modelo de entidade corretamente através do Lombok @Builder")
    void shouldConstructWithLombokBuilder() {
        var item = OrderItemModel.builder().product("Produto B").build();
        var now = LocalDateTime.now();

        var model = OrderModel.builder()
                .id(1L)
                .partnerId(2L)
                .items(List.of(item))
                .totalValue(new BigDecimal("150.00"))
                .status(OrderStatus.APPROVED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(2L, model.getPartnerId());
        assertEquals(1, model.getItems().size());
        assertEquals("Produto B", model.getItems().get(0).getProduct());
        assertEquals(new BigDecimal("150.00"), model.getTotalValue());
        assertEquals(OrderStatus.APPROVED, model.getStatus());
    }

    @Test
    @DisplayName("3. Deve suportar a criação de instâncias com lista de itens inicializada por padrão")
    void shouldSupportNoArgsConstructorWithDefaultListInitialization() {
        var model = new OrderModel();

        assertNull(model.getId());
        assertNull(model.getPartnerId());
        assertNotNull(model.getItems(), "A lista de itens deve ser inicializada vazia por padrão");
        assertTrue(model.getItems().isEmpty());
        assertNull(model.getTotalValue());
        assertNull(model.getStatus());
    }

    @Test
    @DisplayName("4. Deve injetar todos os parâmetros usando o construtor completo")
    void shouldSupportAllArgsConstructor() {
        var now = LocalDateTime.now();
        var model = new OrderModel(
                100L,
                5L,
                new ArrayList<>(),
                new BigDecimal("50.00"),
                OrderStatus.PROCESSING,
                now,
                now
        );

        assertEquals(100L, model.getId());
        assertEquals(OrderStatus.PROCESSING, model.getStatus());
    }

    @Test
    @DisplayName("5. Deve preencher automaticamente as datas de criação e modificação ao disparar o @PrePersist")
    void shouldPopulateTimestampsOnPrePersist() {
        var model = new OrderModel();
        assertNull(model.getCreatedAt());
        assertNull(model.getUpdatedAt());

        model.onCreate();

        assertNotNull(model.getCreatedAt());
        assertNotNull(model.getUpdatedAt());
        assertEquals(model.getCreatedAt(), model.getUpdatedAt());
    }

    @Test
    @DisplayName("6. Deve atualizar apenas a data de modificação ao disparar o @PreUpdate")
    void shouldUpdateTimestampOnPreUpdate() {
        var model = new OrderModel();
        var initialCreatedAt = LocalDateTime.now().minusDays(2);
        model.setCreatedAt(initialCreatedAt);
        model.setUpdatedAt(initialCreatedAt);

        model.onUpdate();

        assertEquals(initialCreatedAt, model.getCreatedAt(), "A data de criação não deve ser alterada no update");
        assertNotEquals(initialCreatedAt, model.getUpdatedAt(), "A data de atualização deve ser renovada");
        assertTrue(model.getUpdatedAt().isAfter(model.getCreatedAt()));
    }
}
