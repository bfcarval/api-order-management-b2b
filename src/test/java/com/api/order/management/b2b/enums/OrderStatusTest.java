package com.api.order.management.b2b.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderStatusTest {

    @Test
    @DisplayName("1. Deve garantir a existência e nomenclatura exata de todos os status mapeados")
    void shouldMaintainEnumContracts() {
        assertNotNull(OrderStatus.valueOf("PENDING"));
        assertNotNull(OrderStatus.valueOf("APPROVED"));
        assertNotNull(OrderStatus.valueOf("PROCESSING"));
        assertNotNull(OrderStatus.valueOf("SEND"));
        assertNotNull(OrderStatus.valueOf("DELIVERED"));
        assertNotNull(OrderStatus.valueOf("CANCELED"));
    }

    @Test
    @DisplayName("2. Deve validar a quantidade total de status da máquina de estados")
    void shouldCheckTotalEnumLength() {
        var statuses = OrderStatus.values();
        assertEquals(6, statuses.length, "A quantidade de status mapeada mudou inadequadamente");
    }

    @Test
    @DisplayName("3. Deve garantir a ordem de declaração (ordinal) correta das constantes")
    void shouldVerifyEnumOrdinals() {
        assertEquals(0, OrderStatus.PENDING.ordinal());
        assertEquals(1, OrderStatus.APPROVED.ordinal());
        assertEquals(2, OrderStatus.PROCESSING.ordinal());
        assertEquals(3, OrderStatus.SEND.ordinal());
        assertEquals(4, OrderStatus.DELIVERED.ordinal());
        assertEquals(5, OrderStatus.CANCELED.ordinal());
    }
}
